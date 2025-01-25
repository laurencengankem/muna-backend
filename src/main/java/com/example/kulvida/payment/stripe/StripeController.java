package com.example.kulvida.payment.stripe;

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
import com.stripe.exception.StripeException;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Slf4j
@RestController
public class StripeController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserOrderRepository userOrdersRepository;


    @Autowired
    private UserItemRepository userItemRepository;


    @Autowired
    private RandomGeneratorUtil generatorUtil;

    @Autowired
    private EmailSenderUtil emailService;

    @Autowired
    private PdfUtil pdfUtil;

    private String frontend_url="http://localhost:4200/";
    private static final String CURRENCY= "eur";

    @PostMapping("/payment/stripe")
    @CrossOrigin
    public ResponseEntity<PaymentResponse> payWithStripe(@RequestBody PaymentRequest request) throws StripeException {

        log.info("Payment request for {}",request);
        PaymentResponse response= new PaymentResponse();

        User user= userRepo.findByUsername(request.getUsername());
        if(user==null){
            throw new RuntimeException();
        }


        List<SessionCreateParams.LineItem> lineItems= new ArrayList<>();
        Gson gson=new Gson();
        CartItem[] items= gson.fromJson(request.getData(),CartItem[].class);

        String orderId= generatorUtil.generateOrderId();
        while(userOrdersRepository.existsByOrderId(orderId)){
            orderId= generatorUtil.generateOrderId();
        }
        List<UserOrder> userOrders= new ArrayList<>();


        for(CartItem item: items){

            Item it= itemRepository.findById(item.getId()).orElse(null);
            UserOrderPk userOrderPk= new UserOrderPk(orderId,item.getId());
            UserOrder userOrder= new UserOrder(userOrderPk,orderId,user,it,
                    new GregorianCalendar(),item.getQuantity(), OrderStatus.INITIALIZED,item.getTotal());
            userOrders.add(userOrder);

            item.setDiscounted(item.getDiscounted()*10);
            lineItems.add(
                    stripeService.createLineItem(
                            Long.parseLong(item.getQuantity().toString()),
                            CURRENCY,
                            //TO MODIFY LATER
                            Long.parseLong(item.getDiscounted().toString().replace(".","")),
                            item.getName())
            );
        }
        userOrdersRepository.saveAll(userOrders);

        response.setSuccess(true);
        response.setPaymentUrl(stripeService.createPaymentSession(lineItems,orderId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/payment/stripe/success")
    public ResponseEntity<?> successfulpayment(@RequestParam String orderId) throws URISyntaxException, IOException {
        log.info("successful stripe payment for order {}",orderId);
        URI frontend = new URI("http://localhost:4200/payment/success");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(frontend);

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


        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }
}
