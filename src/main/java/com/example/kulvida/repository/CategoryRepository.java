package com.example.kulvida.repository;

import com.example.kulvida.entity.cloth.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {
    Category findByName(String name);
}
