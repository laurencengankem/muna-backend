package com.example.kulvida.repository;

import com.example.kulvida.entity.cloth.Order;
import com.example.kulvida.entity.cloth.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {

    List<OrderItem> findByOrderOrderId(String orderId);
}

