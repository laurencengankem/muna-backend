package com.example.kulvida.repository;


import com.example.kulvida.entity.cloth.UpdatedOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpdatedOrderItemRepository extends JpaRepository<UpdatedOrderItem , Integer> {

    List<UpdatedOrderItem> findByOrderUpdateOrderOrderId(String orderId);
}
