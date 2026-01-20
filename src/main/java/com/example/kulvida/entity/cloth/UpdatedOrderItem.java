package com.example.kulvida.entity.cloth;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;

@Entity
@Table(name = "UPDATED_ORDER_ITEMS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_update_id", nullable = false)
    private OrderUpdate orderUpdate;

    @Column(name = "cloth_id")
    private Integer cloth;

    @Column(name = "cloth_name")
    private String clothName;

    @Column(name = "cloth_code")
    private String clothCode;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name="promotion", nullable= false)
    private Double discount;


    @Column(name = "subtotal")
    private Double subTotal;

    @Column(name = "update_type")
    private String updateType;



    public UpdatedOrderItem(OrderItem item, OrderUpdate orderUpdate){
        this.orderUpdate = orderUpdate;
        this.cloth = item.getCloth();
        this.clothName= item.getClothName();
        this.clothCode = item.getClothCode();
        this.quantity = item.getQuantity();
        this.price = item.getPrice();
        this.size = item.getSize();
        this.discount = item.getDiscount();
        this.subTotal = item.getSubTotal();
    }

}
