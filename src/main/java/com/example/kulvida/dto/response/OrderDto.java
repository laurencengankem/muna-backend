package com.example.kulvida.dto.response;

import com.example.kulvida.entity.cloth.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto implements Comparable<OrderDto>{

    private String orderId;
    private Double total;
    private String username;
    private Date date;
    private Integer discount;
    private String method;
    private  Date updatedOn;
    private  Double paid;
    private boolean updated;

    public OrderDto(Order order){
        this.orderId=order.getOrderId();
        if(order.getUser()!=null)
            this.username= order.getUser().getFirstName()+ " "+order.getUser().getLastName();
        this.date= order.getOrderDate().getTime();
        this.discount= order.getDiscount()!=null? order.getDiscount(): 0;
        this.total= order.getTotal();
        this.method= order.getMethod();
        this.updated = order.getUpdated() != null? order.getUpdated() : false;
        this.updatedOn = order.getUpdatedOn() != null ?  order.getUpdatedOn().getTime() : null;
        this.paid = this.total - this.discount;
        if(this.updated){
            this.paid = (order.getTotal() - order.getDiscount())> order.getOldTotal()? this.paid : order.getOldTotal();
        }

        if(this.method==null)
            this.method= "INCONNU";
    }


    @Override
    public int compareTo(@NotNull OrderDto o) {
        if (this.getDate() == null && o.getDate() == null) return 0;  // both null → equal
        if (this.getDate() == null) return 1;  // null → treated as "last" in descending
        if (o.getDate() == null) return -1;   // other null → this comes first
        return o.getDate().compareTo(this.getDate());  // descending
    }

}
