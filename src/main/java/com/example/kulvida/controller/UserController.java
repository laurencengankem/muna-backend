package com.example.kulvida.controller;

import com.example.kulvida.dto.request.DeleteAddressRequest;
import com.example.kulvida.dto.request.OrderDetailsRequest;
import com.example.kulvida.dto.request.SaveAddressRequest;
import com.example.kulvida.dto.request.UpdateUserCartRequest;
import com.example.kulvida.entity.UserAddress;
import com.example.kulvida.entity.UserItem;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/user")
public interface UserController {
    @PostMapping("/save-address")
    @CrossOrigin
    List<UserAddress> saveUserAddress(@RequestBody SaveAddressRequest request);

    @PostMapping("/update-address")
    @CrossOrigin
    List<UserAddress> updateUserAddress(@RequestBody SaveAddressRequest request);

    @GetMapping("/get-addresses/{user}")
    @CrossOrigin
    List<UserAddress> getUserAddresses(@PathVariable String user);

    @PostMapping("/delete-address")
    @CrossOrigin
    List<UserAddress> deleteUserAddress(@RequestBody DeleteAddressRequest request);

    @PostMapping("/update-userCart")
    @CrossOrigin
    boolean updateUserCart(@RequestBody UpdateUserCartRequest request);

    @GetMapping("/get-userCart/{user}")
    @CrossOrigin
    List<?> getUserCart(@PathVariable String user);

    @PostMapping("/order")
    @CrossOrigin
    Object getOrderDetails(@RequestBody OrderDetailsRequest request);

    @GetMapping(value = "/orders/{username}")
    @CrossOrigin
    Object getUserOrders(@PathVariable String username);
    
    @GetMapping(value = "/detail/{username}")
    @CrossOrigin
    Object getUserDetails(@PathVariable String username);

    @PostMapping("/initialize-order")
    @CrossOrigin
    Object initializeOrder(@RequestBody UpdateUserCartRequest request);
}
