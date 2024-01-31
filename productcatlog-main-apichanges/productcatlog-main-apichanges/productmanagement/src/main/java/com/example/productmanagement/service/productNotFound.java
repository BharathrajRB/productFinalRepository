package com.example.productmanagement.service;

public class productNotFound extends RuntimeException {
  public productNotFound(String message) {
    super(message);
  }

}
