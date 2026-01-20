package com.example.kulvida.repository;

import com.example.kulvida.entity.UserItem;
import com.example.kulvida.entity.UserItemPk;
import com.example.kulvida.entity.cloth.Cloth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserItemRepository extends JpaRepository<UserItem, UserItemPk> {

    @Query(value = "select * from user_items where user_id=:user",nativeQuery = true)
    List<UserItem> getUserItems(@Param("user") Integer user);

    List<UserItem> findByItem(Cloth cloth);


}
