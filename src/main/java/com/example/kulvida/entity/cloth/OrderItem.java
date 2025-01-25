package com.example.kulvida.entity.cloth;


import com.example.kulvida.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.GregorianCalendar;

@Entity
@Table(name = "ORDER_ITEMS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "cloth_id")
    private Cloth cloth;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name="discount", nullable= true)
    private Integer discount;


    @Column(name = "subtotal")
    private Double subTotal;

}
