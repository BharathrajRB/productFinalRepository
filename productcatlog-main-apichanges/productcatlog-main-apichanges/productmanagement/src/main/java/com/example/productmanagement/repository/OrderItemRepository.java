package com.example.productmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.productmanagement.modal.OrderItem;
import com.example.productmanagement.modal.User;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderUser(User user);
}
