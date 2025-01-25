package com.example.kulvida.dto.response;

import com.example.kulvida.entity.cloth.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    private String item;
    private Integer quantity;
    private Double price;
    private Double total;

    public OrderItemDto(OrderItem item){
        this.item= item.getCloth().getName();
        this.quantity= item.getQuantity();
        this.price= item.getPrice();
        this.total= item.getSubTotal();
    }
}
