package com.example.kulvida.entity.cloth;

import com.example.kulvida.domain.enums.OrderStatus;
import com.example.kulvida.entity.Item;
import com.example.kulvida.entity.User;
import com.example.kulvida.entity.UserOrderPk;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.GregorianCalendar;
import java.util.List;

@Entity
@Table(name = "USER_ORDER")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order implements Comparable {


	@Id
	@Column(name = "order_id",insertable = false,updatable = false)
	private String orderId;

	@ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
	
	@Column(name = "order_date")
	private GregorianCalendar orderDate;

	@Column(name = "updated_on")
	private GregorianCalendar updatedOn;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column(name = "total")
	private Double total;

	@Column(name = "old_total")
	private Double oldTotal;

	@Column(name = "discount")
	private Integer discount;

	@Column(name = "method")
	private String method;

	@Column(name = "comment")
	private String comment;

	@Column(name="updated")
	private Boolean updated;

	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<OrderUpdate> updateList;

	@PostLoad
	public void setDiscountToZero(){
		this.discount = this.discount == null ? 0 : this.discount;
	}


	@Override
	public int compareTo(@NotNull Object o) {
		if(this.getOrderDate().before(((Order)o).getOrderDate()))
			return 1;
		else if(this.getOrderDate().after(((Order)o).getOrderDate()))
			return -1;
		else return 0;
	}


}
