package com.example.productmanagement.controller;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.productmanagement.modal.Product;
import com.example.productmanagement.modal.User;
import com.example.productmanagement.repository.ProductRepository;
import com.example.productmanagement.service.ProductService;
import com.example.productmanagement.service.UserService;

@RestController

public class ProductController {
  @Autowired
  private ProductService productService;
  @Autowired
  private UserService userService;
  @Autowired
  private ProductRepository productRepository;

  @PostMapping("/products")
  public ResponseEntity<String> createProduct(@RequestHeader("Authorization") String authHeader,
      @RequestBody Product product) {
    try {
      String credentials = new String(Base64.getDecoder().decode(authHeader.split(" ")[1]));
      String[] splitCredentials = credentials.split(":");
      String email = splitCredentials[0];
      String password = splitCredentials[1];
      User user = userService.findByEmailAndPassword(email, password);
      if (user != null && "admin".equals(user.getRole_id().getName())) {
        if (product.getAvailableStock() > 0) {
          productService.createProduct(product);
          return new ResponseEntity<>("Product created successfully", HttpStatus.OK);
        } else {
          return new ResponseEntity<>("available stock is less than zero", HttpStatus.BAD_REQUEST);
        }

      } else {
        return new ResponseEntity<>("You are not authorized to perform this operation", HttpStatus.FORBIDDEN);
      }
    } catch (AuthorizationException e) {
      return new ResponseEntity<>("Error during product creation", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/products/{productId}")
  public ResponseEntity<String> updateProduct(@RequestHeader("Authorization") String authHeader,
      @PathVariable Long productId, @RequestBody Product updateProduct) {
    try {
      String credentials = new String(Base64.getDecoder().decode(authHeader.split(" ")[1]));
      String[] splitCredentials = credentials.split(":");
      String email = splitCredentials[0];
      String password = splitCredentials[1];
      User user = userService.findByEmailAndPassword(email, password);
      if (user != null && "admin".equals(user.getRole_id().getName())) {
        productService.updateProduct(productId, updateProduct);
        return new ResponseEntity<String>("Products updated successfully", HttpStatus.OK);
      } else {
        return new ResponseEntity<String>("you are not an admin", HttpStatus.FORBIDDEN);
      }

    } catch (AuthorizationException e) {

      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/products/{productId}")
  public ResponseEntity<String> deleteProduct(@RequestHeader("Authorization") String authHeader,
      @PathVariable("productId") Long id) {
    try {
      System.out.println("delete");
      String credentials = new String(Base64.getDecoder().decode(authHeader.split(" ")[1]));
      String[] splitCredentials = credentials.split(":");
      String email = splitCredentials[0];
      String password = splitCredentials[1];
      User user = userService.findByEmailAndPassword(email, password);
      if (user != null && "admin".equals(user.getRole_id().getName())) {
        productService.deleteProduct(id);
        return new ResponseEntity<>("deleted successfully", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("you are not allowed to delete the product", HttpStatus.FORBIDDEN);
      }

    } catch (AuthorizationException e) {
      return new ResponseEntity<>("Error during product delete", HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/products")
  List<Product> getProducts() {
    List<Product> products = productService.findAllProducts();
    return products;
  }

  @GetMapping("/products/{productId}")
  public Product getProductById(@PathVariable("productId") Long productId) {
    return productService.getProductById(productId);
  }

}
