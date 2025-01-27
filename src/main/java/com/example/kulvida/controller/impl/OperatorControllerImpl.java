package com.example.kulvida.controller.impl;

import com.example.kulvida.cache.BaseCacheManager;
import com.example.kulvida.controller.OperatorController;
import com.example.kulvida.domain.enums.OrderStatus;
import com.example.kulvida.domain.enums.UserRole;
import com.example.kulvida.domain.models.CartItem;
import com.example.kulvida.dto.request.*;
import com.example.kulvida.dto.response.OrderDto;
import com.example.kulvida.dto.response.OrderItemDto;
import com.example.kulvida.dto.response.ValidateCheckoutResponse;
import com.example.kulvida.entity.User;
import com.example.kulvida.entity.UserItem;
import com.example.kulvida.entity.cloth.Cloth;
import com.example.kulvida.entity.cloth.ClothSize;
import com.example.kulvida.entity.cloth.Order;
import com.example.kulvida.entity.cloth.OrderItem;
import com.example.kulvida.repository.*;
import com.example.kulvida.service.JwtUserDetailsService;
import com.example.kulvida.utils.EmailSenderUtil;
import com.example.kulvida.utils.PdfUtil;
import com.example.kulvida.utils.RandomGeneratorUtil;
import com.example.kulvida.utils.ReceipMunaUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
    private UserOrderRepository userOrderRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    RandomGeneratorUtil randomGeneratorUtil;

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

        Order order=new Order();
        String orderId= randomGeneratorUtil.generateOrderId();
        while(orderRepository.existsById(orderId)){
            orderId= randomGeneratorUtil.generateOrderId();
        }
        order.setOrderId(orderId);
        order.setUser(user);
        order.setOrderDate(new GregorianCalendar());
        order.setStatus(OrderStatus.COMPLETED);
        order= orderRepository.saveAndFlush(order);

        List<OrderItem> orderItems= new ArrayList<>();
        for(ValidateCheckoutItem item: request.getItems()){
            Cloth cloth= clothRepository.findById(item.getId()).orElse(null);
            if(cloth!=null){
                OrderItem orderItem= new OrderItem();
                orderItem.setOrder(order);
                orderItem.setCloth(cloth);
                orderItem.setDiscount(cloth.getDiscount());
                if(cloth.getClothSizes()!=null && !cloth.getClothSizes().isEmpty()){
                    List<SizeRequest> sr= cloth.getClothSizes().stream().map(SizeRequest::new).collect(Collectors.toList());
                    sr.forEach(s->{if(s.getName().equalsIgnoreCase(item.getSize())){
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
                    }});
                }

            }else{
                log.error("clothId {} not found ",item.getId());
            }
        }
        try{
            double total=0;
            for(OrderItem ord: orderItems){
              total+=ord.getSubTotal();
              orderItemRepository.save(ord);
            }
            order.setTotal(total);
            orderRepository.save(order);
            String base64= ReceipMunaUtil.generateReceipt(orderItems);
            return splitBase64(base64,1000);
        }catch (Exception ex){
            log.info(ex.getMessage());
        }

        return null;
    }

    @Override
    public Object generateReceipt(String orderId){

        List<OrderItem> orderItems= orderItemRepository.findByOrderOrderId(orderId);
        if(orderItems!=null && !orderItems.isEmpty()){
            String base64= ReceipMunaUtil.generateReceipt(orderItems);
            return splitBase64(base64,1000);
        }
        return null;
    }

    @Override
    public List<OrderItemDto> getOrderItems(String orderId) {

        return orderItemRepository.findByOrderOrderId(orderId).stream().map(OrderItemDto::new).collect(Collectors.toList());
    }

    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders= orderRepository.findAll();
        List<OrderDto> response= new ArrayList<>();
        for(Order order: orders){
            response.add(new OrderDto(order));
        }
        return response;
    }

    public static List<String> splitBase64(String base64String, int chunkSize) {
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < base64String.length(); i += chunkSize) {
            parts.add(base64String.substring(i, Math.min(i + chunkSize, base64String.length())));
        }
        return parts;
    }
}
