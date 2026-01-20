package com.example.kulvida.repository;


import com.example.kulvida.entity.cloth.OrderUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderUpdateRepository extends JpaRepository<OrderUpdate,Integer> {
    List<OrderUpdate> findByOrderOrderIdOrderByUpdatedOnDesc(String orderId);
}
