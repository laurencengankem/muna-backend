package com.example.kulvida.repository;

import com.example.kulvida.entity.ItemPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPictureRepository extends JpaRepository<ItemPicture,Integer> {

    @Query(value = "select url from item_pictures where item_id=:item",nativeQuery = true)
    List<String> findPicturesUrls(@Param("item") int itemId);

    @Query(value = "select * from item_pictures where item_id=:itemId and url=:url",nativeQuery = true)
    ItemPicture findPicture(@Param("itemId") int itemId,@Param("url") String url);

}
