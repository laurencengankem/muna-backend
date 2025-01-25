package com.example.kulvida.dto.response;

import com.example.kulvida.entity.cloth.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String orderId;
    private Double total;
    private String username;
    private Date date;

    public OrderDto(Order order){
        this.orderId=order.getOrderId();
        this.total= order.getTotal();
        this.username= order.getUser().getFirstName()+ " "+order.getUser().getLastName();
        this.date= order.getOrderDate().getTime();
    }
}
