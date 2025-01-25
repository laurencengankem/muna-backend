package com.example.kulvida.repository;

import com.example.kulvida.entity.UserOrder;
import com.example.kulvida.entity.UserOrderPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder, UserOrderPk> {

    List<UserOrder> findByOrderId(String orderId);

    boolean existsByOrderId(String orderId);

    List<UserOrder> findByUserId(int userId);


}
