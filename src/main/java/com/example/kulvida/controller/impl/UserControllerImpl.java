package com.example.kulvida.controller.impl;

import com.example.kulvida.controller.UserController;
import com.example.kulvida.domain.models.CartItem;
import com.example.kulvida.domain.enums.OrderStatus;
import com.example.kulvida.dto.request.*;
import com.example.kulvida.entity.*;
import com.example.kulvida.entity.cloth.Cloth;
import com.example.kulvida.entity.cloth.ClothSize;
import com.example.kulvida.entity.cloth.Order;
import com.example.kulvida.entity.cloth.OrderItem;
import com.example.kulvida.repository.*;
import com.example.kulvida.utils.EmailSenderUtil;
import com.example.kulvida.utils.PdfUtil;
import com.example.kulvida.utils.RandomGeneratorUtil;
import com.example.kulvida.utils.ReceipMunaUtil;
import com.google.gson.Gson;
import com.stripe.param.terminal.ReaderSetReaderDisplayParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserControllerImpl implements UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UserItemRepository userItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ClothRepository clothRepository;

    @Autowired
    private ClothSizeRepository clothSizeRepository;

    @Autowired
    private UserOrderRepository userOrderRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    RandomGeneratorUtil randomGeneratorUtil;

    @Autowired
    private PdfUtil pdfUtil;


    @Autowired
    private EmailSenderUtil emailService;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");


    @Override
    public List<UserAddress> saveUserAddress(@RequestBody SaveAddressRequest request){
        log.info("{}",request);
        if(userRepo.existsByUsername(request.getUser())){
            User user= userRepo.findByUsername(request.getUser());
            UserAddress userAddress= new UserAddress();
            BeanUtils.copyProperties(request,userAddress);
            userAddress.setId(userAddressRepository.getNextId()!=null?userAddressRepository.getNextId()+1:1);
            user.getAddresses().add(userAddress);
            log.info("{}",user);
            userRepo.save(user);
            return user.getAddresses();
        }
        return null;
    }

    @Override
    public List<UserAddress>updateUserAddress(@RequestBody SaveAddressRequest request){
        log.info("{}",request);
        if(request.getId()!=null && userAddressRepository.existsById(request.getId())){
            UserAddress userAddress= userAddressRepository.findById(request.getId()).orElse(null);
            BeanUtils.copyProperties(request,userAddress);
            userAddressRepository.save(userAddress);
            User user= userRepo.findByUsername(request.getUser());
            return userAddressRepository.getUserAddresses(user.getId());
        }
        return null;
    }


    @Override
    public List<UserAddress> getUserAddresses(@PathVariable String user){
        User us= userRepo.findByUsername(user);
        Integer userId= us!=null? us.getId(): null;
        if(userId!=null){
             return userAddressRepository.getUserAddresses(userId);
        }
        return null;
    }

    @Override
    public List<UserAddress> deleteUserAddress(@RequestBody DeleteAddressRequest request){
        User us= userRepo.findByUsername(request.getUsername());
        Integer userId= us!=null? us.getId(): null;
        if(userId!=null){
            userAddressRepository.deleteById(request.getAddressId());
            return userAddressRepository.getUserAddresses(userId);
        }
        return null;
    }

    @Override
    public boolean updateUserCart(@RequestBody UpdateUserCartRequest request){
        log.info("{}",request);
        User user= userRepo.findByUsername(request.getUsername());
        Gson gson=new Gson();
        CartItem[] items= gson.fromJson(request.getCartData(),CartItem[].class);
        log.info("{}",items);
        List<UserItem> savedUserItems= userItemRepository.getUserItems(user.getId());
        List<String> updateditemsIds= Arrays.stream(items).map(ci->{return ci.getId()+ci.getRequestedSize();}).collect(Collectors.toList());

        for(UserItem userItem: savedUserItems){
            if( !updateditemsIds.contains(userItem.getItem().getClothId()+userItem.getSize())){
                userItemRepository.delete(userItem);
            }
        }

        for(CartItem item:items){
            UserItem userItem= new UserItem();
            userItem.setId(new UserItemPk(item.getId(),user.getId()));
            userItem.setUser(user);
            userItem.setItem(clothRepository.findById(item.getId()).orElse(null));
            userItem.setQuantity(item.getQuantity());
            userItem.setSize(item.getRequestedSize());
            userItemRepository.save(userItem);
        }
        return true;
    }

    @Override
    public List<?> getUserCart(@PathVariable String user){
        User us= userRepo.findByUsername(user);
        if(us!=null){
            List<UserItem> userItems= userItemRepository.getUserItems(us.getId());
            List<CartItem> cart= new ArrayList<>();
            userItems.forEach(ust->{
                CartItem cartItem= new CartItem();
                cartItem.setId(ust.getItem().getClothId());
                cartItem.setRequestedSize(ust.getSize());
                cartItem.setQuantity(ust.getQuantity());
                cartItem.setDiscount(ust.getItem().getDiscount());
                cartItem.setName(ust.getItem().getName());
                cartItem.setAvailable(ust.getItem().getAvailable());
                List<SizeRequest> sr=null;
                if(ust.getItem().getClothSizes()!=null && !ust.getItem().getClothSizes().isEmpty()){
                    sr= ust.getItem().getClothSizes().stream().map(SizeRequest::new).collect(Collectors.toList());
                    sr.stream().forEach(s->{if(s.getName().equalsIgnoreCase(ust.getSize())){
                        cartItem.setPrice(s.getPrice());
                    }});
                    cartItem.setSizes(sr);
                }
                cartItem.setDiscounted(cartItem.getPrice()*(double)(1-((double)cartItem.getDiscount()/100)));
                cartItem.setTotal(cartItem.getDiscounted()*cartItem.getQuantity());
                cart.add(cartItem);
            });
            return cart;
        }
        return null;
    }


    @Override
    @PostMapping("/order")
    @CrossOrigin
    public Object getOrderDetails(@RequestBody OrderDetailsRequest request){
        log.info("{}",request);

        List<UserOrder> order= userOrderRepository.findByOrderId(request.getOrderId());
        if(order==null || order.isEmpty()){
            throw new RuntimeException();
        }
        if(order!=null && !order.get(0).getUser().getUsername().equals(request.getUsername())
        && !order.get(0).getStatus().equals(OrderStatus.COMPLETED)){
            throw new RuntimeException();
        }

        List<Map<String,Object>> response= new ArrayList<>();
        for(UserOrder uo: order){
            ItemPicture pic= uo.getItem().getPictures().stream().findAny().orElse(null);
            String img= pic!=null? pic.getUrl(): "";
            HashMap<String,Object> map= new HashMap<>();
            map.put("item",uo.getItem());
            map.put("quantity",uo.getQuantity());
            map.put("subtotal",uo.getSubTotal());
            map.put("image",img);
            map.put("date",sdf.format(uo.getOrderDate().getTime()));
            map.put("status", uo.getStatus());
            response.add(map);
        }
        return response;
    }

    @Override
    @GetMapping(value = "/orders/{username}")
    @CrossOrigin
    public Object getUserOrders(@PathVariable String username){
        User user= userRepo.findByUsername(username);

        List<UserOrder> orders= userOrderRepository.findByUserId(user.getId());
        Collections.sort(orders);
        List<String> orderIds;
        orderIds = orders.stream().filter(userOrder -> userOrder.getStatus().equals(OrderStatus.COMPLETED)).map(UserOrder::getOrderId).distinct().collect(Collectors.toList());
        List<Map<String,Object>> response= new ArrayList<>();
        for(String orderId: orderIds){
            List<UserOrder> uo=orders.stream().filter(o-> Objects.equals(o.getOrderId(), orderId)).collect(Collectors.toList());
            List<String> imgs= new ArrayList<>();
            imgs= uo.stream().map(o->o.getItem().getMainPictureUrl()).collect(Collectors.toList());
            HashMap<String,Object> map= new HashMap<>();
            map.put("orderId",orderId);
            map.put("date",sdf.format(uo.get(0).getOrderDate().getTime()));
            map.put("images",imgs);
            response.add(map);
        }
        return response;
    }

	@Override
	public Object getUserDetails(String username) {
		User user = userRepo.findByUsername(username);
		user.setPassword(null);
		
		return user;
	}

    @Override
    @Transactional
    public Object initializeOrder(UpdateUserCartRequest request) {
        log.info("{}",request);
        User user= userRepo.findByUsername(request.getUsername());
        Gson gson=new Gson();
        CartItem[] items= gson.fromJson(request.getCartData(),CartItem[].class);
        List<UserItem> savedUserItems= userItemRepository.getUserItems(user.getId());

        Order order=new Order();
        String orderId= randomGeneratorUtil.generateOrderId();
        while(orderRepository.existsById(orderId)){
            orderId= randomGeneratorUtil.generateOrderId();
        }
        order.setOrderId(orderId);
        order.setUser(user);
        order.setOrderDate(new GregorianCalendar());
        order.setStatus(OrderStatus.INITIALIZED);
        order= orderRepository.saveAndFlush(order);

        List<OrderItem> orderItems= new ArrayList<>();
        for(CartItem item: items){
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
                    sr.forEach(s->{if(s.getName().equalsIgnoreCase(item.getRequestedSize())){
                        if(s.getQuantity()>=item.getQuantity()){
                            ClothSize clothSize= cloth.getClothSizes().stream().filter(cs->cs.getSize().getName().equalsIgnoreCase(s.getName())).findFirst().orElse(null);
                            assert clothSize != null;
                            clothSize.setQuantity(clothSize.getQuantity()-item.getQuantity());
                            clothSizeRepository.save(clothSize);
                            orderItem.setSize(clothSize.getSize());
                            orderItem.setQuantity(item.getQuantity());
                            orderItem.setPrice(s.getPrice());
                            orderItem.setSubTotal(item.getDiscounted()*item.getQuantity());
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
        log.info("-------------Arrived Here ------------");
        try{
            double total=0;
            for(OrderItem ord: orderItems){
                total+=ord.getSubTotal();
                orderItemRepository.save(ord);
            }
            order.setTotal(total);
            orderRepository.save(order);
            userItemRepository.deleteAll(savedUserItems);
            String base64= ReceipMunaUtil.generateReceipt(orderItems);
            return splitBase64(base64,1000);
        }catch (Exception ex){
            log.info(ex.getMessage());
        }

        return null;
    }


    public static List<String> splitBase64(String base64String, int chunkSize) {
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < base64String.length(); i += chunkSize) {
            parts.add(base64String.substring(i, Math.min(i + chunkSize, base64String.length())));
        }
        return parts;
    }
}
