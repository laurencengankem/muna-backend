package com.example.kulvida.entity;

import com.example.kulvida.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.GregorianCalendar;

@Entity
@Table(name = "Orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserOrder implements Comparable {
	
	@EmbeddedId
	UserOrderPk id;

	@MapsId("order_id")
	@Column(name = "order_id",insertable = false,updatable = false)
	private String orderId;

	@ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
	@MapsId("item_id")
    @JoinColumn(name = "item_id")
    private Item item;
	
	@Column(name = "order_date")
	private GregorianCalendar orderDate;
	
	@Column(name = "quantity", nullable = false)
    private Integer quantity;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column(name = "subtotal")
	private Double subTotal;


	@Override
	public int compareTo(@NotNull Object o) {
		if(this.getOrderDate().before(((UserOrder)o).getOrderDate()))
			return 1;
		else if(this.getOrderDate().after(((UserOrder)o).getOrderDate()))
			return -1;
		else return 0;
	}
}
