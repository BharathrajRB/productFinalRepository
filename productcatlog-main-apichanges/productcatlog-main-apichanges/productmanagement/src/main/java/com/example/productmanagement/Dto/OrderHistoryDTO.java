package com.example.productmanagement.Dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OrderHistoryDTO {
    private Long orderId;
    private Timestamp orderDate;
    private String paymentMethod;
    private BigDecimal totalAmount;
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public Timestamp getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public int getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    private int totalCount;
 

    // public OrderHistoryDTO(Long orderId, Timestamp orderDate, BigDecimal price, String payment_id) {
    //     this.orderId = orderId;
    //     this.orderDate = orderDate;
    //     this.price = price;
    //     this.payment_id = payment_id;
    // }

   

    // public Long getOrderId() {
    //     return orderId;
    // }

    // public void setOrderId(Long orderId) {
    //     this.orderId = orderId;
    // }

    // public Timestamp getOrderDate() {
    //     return orderDate;
    // }

    // public void setOrderDate(Timestamp orderDate) {
    //     this.orderDate = orderDate;
    // }

    // public String getPayment_id() {
    //     return payment_id;
    // }

    // public void setPayment_id(String payment_id) {
    //     this.payment_id = payment_id;
    // }

    // public BigDecimal getPrice() {
    // return price;
    // }

    // public void setPrice(BigDecimal price) {
    // this.price = price;
    // }

}
