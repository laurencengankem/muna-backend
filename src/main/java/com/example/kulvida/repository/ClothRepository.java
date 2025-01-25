package com.example.kulvida.repository;

import com.example.kulvida.entity.Item;
import com.example.kulvida.entity.cloth.Category;
import com.example.kulvida.entity.cloth.Cloth;
import com.example.kulvida.entity.cloth.Sex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothRepository extends JpaRepository<Cloth, Integer> {


    @Query("select c from Cloth c where c.name= :input")
    List<Cloth> findByName(@Param("input") String name);

    // Method to check if a Cloth entity exists by its name (uses derived query method)
    Boolean existsByName(String name);

    // JPQL query to check the availability of a Cloth entity by name
    @Query("select c.available from Cloth c where c.available is not null and c.name = :name")
    boolean checkAvalability(@Param("name") String name);

    @Query("SELECT c FROM Cloth c WHERE " +
            "(:input IS NULL OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :input, '%')) OR " +
            "LOWER(c.category.name) LIKE LOWER(CONCAT('%', :input, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :input, '%')))")
    List<Cloth> searchByInput(@Param("input") String input);

    List<Cloth> findByCategoryAndSex(Category category, Sex sex);
}
