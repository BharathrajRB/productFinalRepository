package com.example.productmanagement.controller;

public class AuthorizationException extends RuntimeException {
  public AuthorizationException(String message) {
    super(message);
  }
}
