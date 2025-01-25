package com.example.kulvida.payment.paypal;

import com.example.kulvida.cache.BaseCacheManager;
import com.example.kulvida.domain.models.CartItem;
import com.example.kulvida.domain.enums.OrderStatus;
import com.example.kulvida.dto.request.PaymentRequest;
import com.example.kulvida.dto.response.PaymentResponse;
import com.example.kulvida.entity.*;
import com.example.kulvida.repository.ItemRepository;
import com.example.kulvida.repository.UserItemRepository;
import com.example.kulvida.repository.UserOrderRepository;
import com.example.kulvida.repository.UserRepository;
import com.example.kulvida.utils.EmailSenderUtil;
import com.example.kulvida.utils.PdfUtil;
import com.example.kulvida.utils.RandomGeneratorUtil;
import com.google.gson.Gson;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@RestController
@Slf4j
public class PaypalController {

    @Autowired
    private PaypalService service;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserItemRepository userItemRepository;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserOrderRepository userOrdersRepository;

    @Autowired
    private BaseCacheManager cacheManager;

    @Autowired
    private RandomGeneratorUtil generatorUtil;

    @Autowired
    private PdfUtil pdfUtil;


    @Autowired
    private EmailSenderUtil emailService;


    private String frontend_url="http://localhost:4200/";
    public static final String SUCCESS_URL = "payment/paypal/success?orderId=";
    public static final String CANCEL_URL = "payment/cancel";



    @PostMapping("/payment/paypal")
    @CrossOrigin
    public ResponseEntity<PaymentResponse> payWithPaypal(@RequestBody PaymentRequest request) {

        log.info("Payment request for {}",request);

        User user= userRepo.findByUsername(request.getUsername());
        if(user==null){
            throw new RuntimeException();
        }

        PaymentResponse response= new PaymentResponse();

        String orderId= generatorUtil.generateOrderId();
        while(userOrdersRepository.existsByOrderId(orderId)){
            orderId= generatorUtil.generateOrderId();
        }

        Order order= new Order();
        Gson gson=new Gson();
        CartItem[] items= gson.fromJson(request.getData(),CartItem[].class);
        double total=0;

        List<UserOrder> userOrders= new ArrayList<>();
        for(CartItem item: items){
            Item it= itemRepository.findById(item.getId()).orElse(null);
            UserOrderPk userOrderPk= new UserOrderPk(orderId,item.getId());
            UserOrder userOrder= new UserOrder(userOrderPk,orderId,user,it,
                    new GregorianCalendar(),item.getQuantity(),OrderStatus.INITIALIZED,item.getTotal());
            userOrders.add(userOrder);
            total+= item.getTotal();
        }
        userOrdersRepository.saveAll(userOrders);

        order.setIntent("sale");
        order.setDescription("new order");
        order.setCurrency("EUR");
        order.setMethod("paypal");
        order.setOrderId(orderId);
        order.setPrice(total);

        log.info("order : {}",order);

        final String paypalResponse= payment(order);
        if(!paypalResponse.equals("FAILED")){
            response.setSuccess(true);
            response.setPaymentUrl(paypalResponse);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/payment/pay")
    public String payment(@RequestBody Order order) {
        log.info("Order: {}",order);
        try {
            Payment payment = service.createPayment(order.getPrice(), order.getCurrency(), order.getMethod(),
                    order.getIntent(), order.getDescription(), "http://localhost:4200/" + CANCEL_URL,
                    "http://localhost:9000/" + SUCCESS_URL+order.getOrderId());
            for(Links link:payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    return link.getHref();
                }
            }

        } catch (PayPalRESTException e) {

            e.printStackTrace();
        }
        return "FAILED";
    }


    @GetMapping(value = "payment/paypal/success")
    public ResponseEntity<?> successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId,
                                        @RequestParam("orderId") String orderId) throws URISyntaxException {
        log.info("successful paypal payment for order {}",orderId);
        URI frontend = new URI("http://localhost:4200/payment/success");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(frontend);

        try {
            Payment payment = service.executePayment(paymentId, payerId);
            log.info(payment.toJSON());
            if (payment.getState().equals("approved")) {
                List<UserOrder> userOrders= userOrdersRepository.findByOrderId(orderId);
                List<UserItem> cart= userItemRepository.getUserItems(userOrders.get(0).getUser().getId());
                userOrders.forEach(userOrder -> {
                    userOrder.setStatus(OrderStatus.COMPLETED);
                });
                userItemRepository.deleteAll(cart);
                userOrdersRepository.saveAll(userOrders);


                String toEmail=userOrders.get(0).getUser().getUsername();
                String subject= "receipt of order N°"+orderId;
                String body= "Thank you for your order. You will find your receipt attached to this mail";
                String filepath= "./receipt.pdf";
                String trackingLink= frontend_url+"order/"+orderId;

                pdfUtil.generateReceipt("./receipt.pdf",userOrders,trackingLink);
                emailService.sendReceipt(toEmail,subject,body,filepath);

                httpHeaders.add("result",payment.getState());
                return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}