package com.example.kulvida.repository;

import com.example.kulvida.entity.cloth.ClothPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothPictureRepository extends JpaRepository<ClothPicture,Integer> {

    @Query(value = "select url from cloth_pictures where cloth_id=:item",nativeQuery = true)
    List<String> findPicturesUrls(@Param("item") int itemId);

    @Query(value = "select * from cloth_pictures where cloth_id=:itemId and url=:url",nativeQuery = true)
    ClothPicture findPicture(@Param("itemId") int itemId,@Param("url") String url);

}
