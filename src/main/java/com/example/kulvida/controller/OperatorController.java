package com.example.kulvida.controller;

import com.example.kulvida.dto.request.SendEmailRequest;
import com.example.kulvida.dto.request.ValidateCheckoutRequest;
import com.example.kulvida.dto.response.*;
import com.example.kulvida.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface OperatorController {

    @PostMapping("/admin/create-user")
    @CrossOrigin
    boolean createUser(@RequestBody SendEmailRequest request);


    @PostMapping("/admin/update-user-status")
    @CrossOrigin
    boolean  updateUserStatus(@RequestBody SendEmailRequest request);

    @PostMapping("/admin/editUserPassword")
    @CrossOrigin
    boolean editUserPassword(@RequestBody SendEmailRequest request);


    @GetMapping("/admin/getUserList")
    @CrossOrigin
    List<User> getUserList();


    @PostMapping("/operator/validate-checkout")
    @CrossOrigin
    ValidateCheckoutResponse validateCheckout(@RequestBody ValidateCheckoutRequest request);

    @PostMapping("/operator/complete-checkout")
    @CrossOrigin
    Object completeOrder(@RequestBody ValidateCheckoutRequest request);


    @GetMapping("/operator/generate-receipt/{orderId}")
    @CrossOrigin
    Object generateReceipt(@PathVariable String orderId);

    @GetMapping("/operator/getOrderItems/{orderId}")
    @CrossOrigin
    Map<String,Object> getOrderItems(@PathVariable String orderId);

    @GetMapping("/operator/getOrderList")
    @CrossOrigin
    List<OrderDto> getAllOrders();

    @GetMapping("/operator/getNextCode")
    @CrossOrigin
    ResponseEntity<NextCodeResponse> getNextCode();


    @PostMapping("/operator/testImage")
    @CrossOrigin
    Object generateImage(@RequestBody ValidateCheckoutRequest request);


    @GetMapping("/admin/delete-order/{orderId}")
    @CrossOrigin
    Object deleteOrder(@PathVariable String orderId);

    @GetMapping("/item/getDashboardStats")
    @CrossOrigin
    DashboardCountResponse getDashboardStats();










}
