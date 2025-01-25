package com.example.kulvida.repository;

import com.example.kulvida.entity.cloth.Cloth;
import com.example.kulvida.entity.cloth.ClothSize;
import com.example.kulvida.entity.cloth.ClothSizeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothSizeRepository extends JpaRepository<ClothSize,ClothSizeId> {

    List<ClothSize> findByCloth(Cloth cloth);

}
