package com.example.kulvida.controller;

import com.example.kulvida.dto.request.SendEmailRequest;
import com.example.kulvida.dto.request.ValidateCheckoutRequest;
import com.example.kulvida.dto.response.OrderDto;
import com.example.kulvida.dto.response.OrderItemDto;
import com.example.kulvida.dto.response.ValidateCheckoutResponse;
import com.example.kulvida.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    List<OrderItemDto> getOrderItems(@PathVariable String orderId);

    @GetMapping("/operator/getOrderList")
    @CrossOrigin
    List<OrderDto> getAllOrders();







}
