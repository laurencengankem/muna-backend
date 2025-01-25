package com.example.kulvida.repository;

import com.example.kulvida.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item,Integer> {

    @Query(value = "select * from items where name like %:input%",nativeQuery = true)
    List<Item> findByName(@Param("input")String name);

    Boolean existsByName(String name);

    @Query(value = "select available from items where available is not null and name=:name",nativeQuery = true)
    boolean checkAvalability(@Param("name") String name);


}
