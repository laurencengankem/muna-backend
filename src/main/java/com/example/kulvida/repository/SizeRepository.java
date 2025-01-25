package com.example.kulvida.repository;

import com.example.kulvida.entity.cloth.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepository extends JpaRepository<Size,Integer> {

    Size findByName(String name);
}
