package com.example.kulvida.dto.response;

import com.example.kulvida.entity.cloth.OrderItem;
import com.example.kulvida.entity.cloth.UpdatedOrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    private Integer orderItemId;
    private String item;
    private Integer quantity;
    private Double price;
    private Double total;
    private String code;
    private String description;
    private String date;

    public OrderItemDto(OrderItem item){
        this.item= item.getClothName();
        this.quantity= item.getQuantity();
        this.price= item.getPrice();
        this.total= item.getSubTotal();
        this.code= item.getClothCode()+item.getSize().getName();
        this.orderItemId = item.getId();
    }

    public OrderItemDto(UpdatedOrderItem item){
        this.item= item.getClothName();
        this.quantity= item.getQuantity();
        this.price= item.getPrice();
        this.total= item.getSubTotal();
        this.code= item.getClothCode()+item.getSize().getName();
        this.orderItemId = item.getId();
        this.description = item.getUpdateType().equalsIgnoreCase("ADDED")? "Ajouté":"Rétiré";
        this.date = (new SimpleDateFormat("dd/MM/yyyy hh:mm")).format(item.getOrderUpdate().getUpdatedOn().getTime());
    }


}
