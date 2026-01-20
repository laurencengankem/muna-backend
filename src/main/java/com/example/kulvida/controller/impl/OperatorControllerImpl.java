package com.example.kulvida.controller.impl;

import com.example.kulvida.controller.OperatorController;
import com.example.kulvida.domain.enums.OrderStatus;
import com.example.kulvida.domain.enums.UserRole;
import com.example.kulvida.dto.request.SendEmailRequest;
import com.example.kulvida.dto.request.SizeRequest;
import com.example.kulvida.dto.request.ValidateCheckoutItem;
import com.example.kulvida.dto.request.ValidateCheckoutRequest;
import com.example.kulvida.dto.response.*;
import com.example.kulvida.entity.User;
import com.example.kulvida.entity.cloth.*;
import com.example.kulvida.repository.*;
import com.example.kulvida.service.JwtUserDetailsService;
import com.example.kulvida.utils.EmailSenderUtil;
import com.example.kulvida.utils.PdfUtilNew;
import com.example.kulvida.utils.RandomGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@Slf4j
public class OperatorControllerImpl implements OperatorController {

    @Autowired
    private EmailSenderUtil senderService;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ClothRepository clothRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ClothSizeRepository clothSizeRepository;


    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UserItemRepository userItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private UserOrderRepository userOrderRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderUpdateRepository orderUpdateRepository;

    @Autowired
    UpdatedOrderItemRepository updatedOrderItemRepository;

    @Autowired
    RandomGeneratorUtil randomGeneratorUtil;

    @Autowired
    PdfUtilNew pdfUtil;

    @Autowired
    private PasswordEncoder bcryptEncoder;


    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");


    @Override
    public boolean createUser(SendEmailRequest request) {

        if(!userRepo.existsByUsername(request.getEmail())) {
            log.info("{}", request);

            //int max = 999999;
            //int min = 100000;
            //int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);

            User user= new User(request.getEmail(),request.getPassword());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setStatus("ACTIVE");
            user.setCreatedOn(new Date());

            for(UserRole us: UserRole.values()){
                if(us.getValue().equals(request.getRole()))
                    user.setRole(us);
            }
            user= userDetailsService.save(user);

            String subject = "Account Creation";
            String body = "Hello! Your account has been successfully created.\n \n" +
                    "ROLE: "+ user.getRole().getValue()+ "\n"+
                    "USERNAME: "+ user.getUsername()+ "\n"+
                    "PASSWORD: " + request.getPassword();
            senderService.sendEmail(request.getEmail(), subject, body);

            return true;
        }

        return false;


    }

    @Override
    public boolean updateUserStatus(SendEmailRequest request) {
        User user = userRepo.findByUsername(request.getEmail());
        if(user!=null && !user.getRole().getValue().equals("ADMIN")){
            if(user.getStatus()==null || user.getStatus().equals("ACTIVE"))
                user.setStatus("INACTIVE");
            else user.setStatus("ACTIVE");
            user.setUpdatedOn(new Date());
            userRepo.save(user);

            return true;
        }
        return false;
    }

    @Override
    public boolean editUserPassword(@RequestBody SendEmailRequest request){
        User user = userRepo.findByUsername(request.getEmail());
        if(user!=null && user.getRole().getValue().equals("OPERATOR")){
            user.setPassword(bcryptEncoder.encode(request.getPassword()));
            user.setUpdatedOn(new Date());
            userRepo.save(user);

            String subject = "Mot De Passe Modifié";
            String body = "Votre Mot De Passe a été Modifié. " +
                    "Vos données d'accès au système Muna sont désormais celles ci-dessous.\n \n" +
                    "USERNAME: "+ user.getUsername()+ "\n"+
                    "PASSWORD: " + request.getPassword();
            senderService.sendEmail(request.getEmail(), subject, body);

            return true;
        }
        return false;
    }

    @Override
    public List<User> getUserList() {
        List<User> users= userRepo.findAll();
        users.forEach(us->{
            us.setPassword(null);
            if(us.getStatus()==null)
                us.setStatus("ACTIVE");
        });
        return users;
    }

    @Override
    public ValidateCheckoutResponse validateCheckout(ValidateCheckoutRequest request) {

        log.info("{}",request);
        ValidateCheckoutResponse response= new ValidateCheckoutResponse();

        for(ValidateCheckoutItem item: request.getItems()){
            Cloth cloth= clothRepository.findById(item.getId()).orElse(null);
            if(cloth!=null){
                boolean sizeFound= false;
                for(ClothSize cs: cloth.getClothSizes()){
                    if(cs.getSize().getName().equals(item.getSize())){
                        sizeFound=true;
                        if(cs.getQuantity()< item.getQuantity()){
                            response.setStatus(400);
                            String msg= String.format("La taille %s de l'article %s n'est pas disponible dans la quantité requise (%d). Quantité disponible: %d",item.getSize(),item.getName(),item.getQuantity(),cs.getQuantity());
                            response.getMessages().add(msg);
                        }
                        break;
                    }
                }
                if(!sizeFound){
                    response.setStatus(400);
                    String msg= String.format("L'article %s n'est pas disponible dans la taille requise (%s)",item.getName(),item.getSize());
                    response.getMessages().add(msg);
                }
            }else{
                response.setStatus(400);
                String msg= String.format("L'article %s n'est pas disponible dans notre catalogue.",item.getName());
                response.getMessages().add(msg);
            }

        }


        return response;
    }

    @Override
    @Transactional
    public Object completeOrder(ValidateCheckoutRequest request) {
        log.info("{}",request);
        User user= userRepo.findByUsername(request.getUsername());

        Order order= null;
        OrderUpdate orderUpdate= null;
        List<OrderItem> orderItems= null;
        List<UpdatedOrderItem> updatedOrderItems= new ArrayList<>();
        if(request.isUpdating()){
            order= orderRepository.findById(request.getOldOrderId()).orElse(null);
            assert order != null;
            orderUpdate = new OrderUpdate();
            orderUpdate.setUpdatedOn(new GregorianCalendar());
            orderUpdate.setOldTotal(order.getTotal());
            orderUpdate.setOrder(order);
            orderUpdateRepository.saveAndFlush(orderUpdate);
            order.setDiscount(request.getDiscount());
            order.setUpdatedOn(new GregorianCalendar());
            if(order.getOldTotal()!=null){
                if((order.getTotal() - order.getDiscount())>order.getOldTotal())
                    order.setOldTotal(order.getTotal() - order.getDiscount());
            }else order.setOldTotal(order.getTotal() - order.getDiscount());
            order.setUpdated(true);
            order.setComment(String.format("Commande modifiée par %s",user.getFullName()));
            order= orderRepository.saveAndFlush(order);
            orderItems = orderItemRepository.findByOrderOrderId(request.getOldOrderId());
            List<String> codes= request.getOldItems().stream().map(ValidateCheckoutItem::getCode).collect(Collectors.toList());

            for(OrderItem item: orderItems){
                if(!codes.contains(item.getClothCode()+item.getSize().getName()+item.getId())){
                    UpdatedOrderItem uo = new UpdatedOrderItem(item,orderUpdate);
                    uo.setUpdateType("REMOVED");
                    updatedOrderItems.add(uo);
                }
            }

            orderItemRepository.deleteAll(
                    orderItems.stream().filter(item -> !codes.contains(item.getClothCode()+item.getSize().getName()+item.getId())).collect(Collectors.toList())
            );
            orderItems = orderItemRepository.findByOrderOrderId(request.getOldOrderId());
            final int orderUpdateId = orderUpdate.getId();
            List<UpdatedOrderItem> finalUpdatedOrderItems = updatedOrderItems;
            orderItems.forEach(item ->{
                request.getOldItems().stream().filter(req -> req.getCode().equalsIgnoreCase(item.getClothCode()+item.getSize().getName()+item.getId()) ).forEach(
                        req -> {
                            if(item.getQuantity() - req.getQuantity() >0){
                                UpdatedOrderItem uo = new UpdatedOrderItem(item,orderUpdateRepository.getReferenceById(orderUpdateId));
                                uo.setUpdateType("REDUCED");
                                uo.setQuantity(item.getQuantity() - req.getQuantity());
                                uo.setSubTotal((item.getSubTotal()/item.getQuantity())*uo.getQuantity());
                                finalUpdatedOrderItems.add(uo);
                                item.setQuantity(req.getQuantity()); item.setSubTotal(req.getPrice()*req.getQuantity());

                            }
                        }
                );
            });
        }else{
            order =new Order();
            String orderId= randomGeneratorUtil.generateOrderId();
            while(orderRepository.existsById(orderId)){
                orderId= randomGeneratorUtil.generateOrderId();
            }
            order.setOrderId(orderId);
            order.setUser(user);
            order.setDiscount(request.getDiscount());
            order.setMethod(request.getPaymentMethod());
            order.setOrderDate(new GregorianCalendar());
            order.setStatus(OrderStatus.COMPLETED);
            order= orderRepository.saveAndFlush(order);
            orderItems= new ArrayList<>();
        }

        for(ValidateCheckoutItem item: request.getItems()){
            Cloth cloth= clothRepository.findById(item.getId()).orElse(null);
            if(cloth!=null){
                OrderItem orderItem= new OrderItem();
                orderItem.setOrder(order);
                orderItem.setCloth(cloth.getClothId());
                orderItem.setClothCode(cloth.getCode());
                orderItem.setClothName(cloth.getName());
                orderItem.setDiscount(cloth.getDiscount());
                if(cloth.getClothSizes()!=null && !cloth.getClothSizes().isEmpty()){
                    List<SizeRequest> sr= cloth.getClothSizes().stream().map(SizeRequest::new).collect(Collectors.toList());
                    for(SizeRequest s: sr){
                        if(s.getName().equalsIgnoreCase(item.getSize())){
                            if(s.getQuantity()>=item.getQuantity()){
                                ClothSize clothSize= cloth.getClothSizes().stream().filter(cs->cs.getSize().getName().equalsIgnoreCase(s.getName())).findFirst().orElse(null);
                                assert clothSize != null;
                                clothSize.setQuantity(clothSize.getQuantity()-item.getQuantity());
                                clothSizeRepository.save(clothSize);
                                orderItem.setSize(clothSize.getSize());
                                orderItem.setQuantity(item.getQuantity());
                                orderItem.setPrice(s.getPrice());
                                orderItem.setSubTotal(item.getPrice()*item.getQuantity());
                                orderItems.add(orderItem);
                            }
                            else{
                                log.error("item with unsufficient quantity: {}",item);
                            }
                        }
                    }
                }
                if(request.isUpdating()){
                    UpdatedOrderItem uo = new UpdatedOrderItem(orderItem,orderUpdate);
                    uo.setUpdateType("ADDED");
                    updatedOrderItems.add(uo);
                }

            }else{
                log.error("clothId {} not found ",item.getId());
            }
        }
        try{
            //Restore Quantities
            updatedOrderItems = updatedOrderItemRepository.saveAllAndFlush(updatedOrderItems);
            updatedOrderItems.stream().filter(uo -> ! uo.getUpdateType().equals("ADDED"))
                    .forEach(uo ->{
                Cloth cloth = clothRepository.findById(uo.getCloth()).orElse(null);
                if(cloth!=null && cloth.getName().equalsIgnoreCase(uo.getClothName())){
                    cloth.getClothSizes().forEach(cs ->{
                        if(cs.getSize().getName().equals(uo.getSize().getName())){
                            cs.setQuantity(cs.getQuantity()+uo.getQuantity());
                        }
                    });
                    clothRepository.save(cloth);
                }
            });
            double total=0;
            for(OrderItem ord: orderItems){
              total+=ord.getSubTotal();
              orderItemRepository.save(ord);
            }
            order.setTotal(total);
            orderRepository.save(order);
            try {
                log.info("orderItems: {}",orderItems);
                Map<String,String> result = new HashMap<>();
                result.put("orderId",order.getOrderId());
                return result;
                /*List<OrderUpdate> updates = orderUpdateRepository.findByOrderOrderIdOrderByUpdatedOnDesc(order.getOrderId());
                updates = updates == null ? new ArrayList<>():updates;
                byte[] pdfBytes = pdfUtil.generatePOSReceipt(orderItems,updates);

                if (pdfBytes == null) {
                    return ResponseEntity.internalServerError().body(null);
                }
                return

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipt.pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(pdfBytes);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            //String base64= ReceipMunaUtil.generateReceipt(orderItems);
            //return splitBase64(base64,1000);
        }catch (Exception ex){
            log.info(ex.getMessage());
        }

        return null;
    }

    @Override
    public Object generateReceipt(String orderId){

        Order order = orderRepository.findById(orderId).orElse(null);

        if(order != null){
            //String base64= ReceipMunaUtil.generateReceipt(orderItems);
            List<OrderItem> orderItems= orderItemRepository.findByOrderOrderId(orderId);
            List<OrderUpdate> updatedOrderItems = orderUpdateRepository.findByOrderOrderIdOrderByUpdatedOnDesc(orderId);

            try {
                updatedOrderItems = updatedOrderItems == null ? new ArrayList<>():updatedOrderItems;
                byte[] pdfBytes = pdfUtil.generatePOSReceipt(orderItems,updatedOrderItems,order);

                if (pdfBytes == null) {
                    return ResponseEntity.internalServerError().body(null);
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recipt.pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(pdfBytes);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            //return splitBase64(base64,1000);
        }
        return null;
    }

    @Override
    public Map<String,Object> getOrderItems(String orderId) {
        Map<String,Object> result = new HashMap<>();
        List<OrderItemDto> items = orderItemRepository.findByOrderOrderId(orderId).stream().map(OrderItemDto::new).collect(Collectors.toList());

        List<OrderItemDto> updates = updatedOrderItemRepository.findByOrderUpdateOrderOrderId(orderId).stream().map(OrderItemDto::new).collect(Collectors.toList());


        Order order = orderRepository.findById(orderId).orElse(null);
        if(order!=null){
            double paid = order.getOldTotal() == null? 0 : order.getOldTotal();
            if(paid < (order.getTotal() - order.getDiscount()))
                paid = order.getTotal() - order.getDiscount();
            result.put("paid",paid);
        }else {
            log.info("Order is null");
        }


        result.put("items",items);
        result.put("updates", updates);

        return result;
    }

    @Override
    public List<OrderDto> getAllOrders() {

        return orderRepository.findAll().stream()
                .map(OrderDto::new)
                .sorted() // uses Comparable<OrderDto>
                .collect(Collectors.toList());

    }

    @Override
    public ResponseEntity<NextCodeResponse> getNextCode() {
        Integer current= clothRepository.findNextCode();
        if(current==null)
            return ResponseEntity.ok(new NextCodeResponse("00001"));
        else {
            int next = current +1;
            String padded= "0000"+next;
            return ResponseEntity.ok(new NextCodeResponse( padded.substring(padded.length()-5)));
        }

    }

    @Override
    @Transactional
    public Object deleteOrder(String orderId){
        Order order= orderRepository.findById(orderId).orElse(null);
        if(order !=null){
            List<OrderItem> itemList= orderItemRepository.findByOrderOrderId(orderId);
            itemList.forEach(uo ->{
                Cloth cloth = clothRepository.findById(uo.getCloth()).orElse(null);
                if(cloth!=null && cloth.getName().equalsIgnoreCase(uo.getClothName())){
                    cloth.getClothSizes().forEach(cs ->{
                        if(cs.getSize().getName().equals(uo.getSize().getName())){
                            cs.setQuantity(cs.getQuantity()+uo.getQuantity());
                        }
                    });
                    clothRepository.save(cloth);
                }
            });
            orderItemRepository.deleteAll(itemList);
            orderRepository.delete(order);
            return true;
        }
        return false;
    }

    @Override
    public DashboardCountResponse getDashboardStats() {
        DashboardCountResponse response= new DashboardCountResponse();
        response.setWeeks(dashboardRepository.getSalesByWeek());
        response.setMonths(dashboardRepository.getSalesByWeek());
        response.setDays(dashboardRepository.findOrderCountByDay());
        return response;
    }

    @Override
    public Object generateImage(ValidateCheckoutRequest request) {

        List<OrderItem> orderItemList= orderItemRepository.findAll();
        if(orderItemList.size()>20)
            orderItemList= orderItemList.subList(0,20);

        if(request.getWidth()/request.getFontSize()<28)
            request.setWidth(request.getFontSize()*30);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recipt.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfUtil.generatePOSReceipt(orderItemList,request.getFontSize(),request.getWidth()));

    }

    public static List<String> splitBase64(String base64String, int chunkSize) {
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < base64String.length(); i += chunkSize) {
            parts.add(base64String.substring(i, Math.min(i + chunkSize, base64String.length())));
        }
        return parts;
    }
}
