package com.example.productmanagement.repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.productmanagement.modal.CartItem;
import com.example.productmanagement.modal.Product;
import com.example.productmanagement.modal.User;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  Optional<CartItem> findByUserAndProduct(User user, Product product);

  List<CartItem> findByUser(User user);

}