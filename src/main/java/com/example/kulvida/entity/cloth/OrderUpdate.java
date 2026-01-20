package com.example.kulvida.entity.cloth;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.GregorianCalendar;
import java.util.List;

@Entity
@Table(name = "order_updates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "updated_on")
    private GregorianCalendar updatedOn;

    @Column(name = "old_total")
    private Double oldTotal;

    @OneToMany(mappedBy = "orderUpdate",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<UpdatedOrderItem> updatedOrderItemList;
}
