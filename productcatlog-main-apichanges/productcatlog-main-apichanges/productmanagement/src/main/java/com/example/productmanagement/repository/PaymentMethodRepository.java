package com.example.productmanagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.productmanagement.modal.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
   
  PaymentMethod findById(int paymentMethodId);
  

}
