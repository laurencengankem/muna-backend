package com.example.kulvida.repository;

import com.example.kulvida.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    User findByUsername(String username);
    Boolean existsByUsername(String username);


    
}
