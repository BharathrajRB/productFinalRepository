package com.example.productmanagement.repository;

import com.example.productmanagement.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    User findByEmailAndPassword(String email, String password);

    User findByEmail(String email);
    
}
