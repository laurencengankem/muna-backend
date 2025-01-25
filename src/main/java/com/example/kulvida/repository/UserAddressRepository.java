package com.example.kulvida.repository;

import com.example.kulvida.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress,Integer> {

    @Query(value = "select max(id) from user_address",nativeQuery = true)
    Integer getNextId();

    @Query(value = "select * from user_address where user_id=:user",nativeQuery = true)
    List<UserAddress> getUserAddresses(@Param("user") int id);
}
