package com.example.productmanagement.repository;

import com.example.productmanagement.modal.OrderItem;
import com.example.productmanagement.modal.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import com.example.productmanagement.modal.User;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUser(User user);

    Optional<Orders> findByIdAndUser(Long orderId, User user);

    List<Orders> findByUserId(Long id);
}
