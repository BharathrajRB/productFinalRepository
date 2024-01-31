package com.example.productmanagement.controller;

import com.example.productmanagement.Dto.OrderHistoryDTO;
import com.example.productmanagement.modal.CartItem;
import com.example.productmanagement.modal.OrderItem;
import com.example.productmanagement.modal.Orders;
import com.example.productmanagement.modal.Product;
import com.example.productmanagement.modal.User;
import com.example.productmanagement.repository.CartItemRepository;
import com.example.productmanagement.repository.OrderItemRepository;
import com.example.productmanagement.repository.OrdersRepository;
import com.example.productmanagement.repository.PaymentMethodRepository;
import com.example.productmanagement.repository.ProductRepository;
import com.example.productmanagement.service.UserService;
import com.example.productmanagement.service.ProductService;
import com.example.productmanagement.service.UserAlreadyExistsException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return new ResponseEntity<>("success👍", HttpStatus.OK);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>("User already exists 😞...", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String credentials = new String(Base64.getDecoder().decode(authHeader.split(" ")[1]));
            String[] splitCredentials = credentials.split(":");
            String email = splitCredentials[0];
            String password = splitCredentials[1];

            User user = userService.findByEmailAndPassword(email, password);
            // System.out.println("user " + user);

            if (user != null) {
                String role = user.getRole_id().getName();
                String encodedCredentials = userService.encodeCredentials(email, password);
                System.out.println("Encoded Credentials: " + encodedCredentials);
                return new ResponseEntity<>("Login successful. Role: " + role, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Register your account..", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error during login", HttpStatus.BAD_REQUEST);
        }
    }



    // @GetMapping("/orderhistory")
    // public ResponseEntity<List<OrderHistoryDTO>> getOrderHistory(@RequestHeader("Authorization") String authHeader) {
    //     try {
    //         String credentials = new String(Base64.getDecoder().decode(authHeader.split(" ")[1]));
    //         String splitCredentials[] = credentials.split(":");
    //         String email = splitCredentials[0];
    //         String password = splitCredentials[1];
    //         User user = userService.findByEmailAndPassword(email, password);

    //         if (user != null) {
    //             List<OrderItem> orderItems = orderItemRepository.findByOrderUser(user);
    //             List<Orders> orders = ordersRepository.findByUser(user);
    //             List<OrderHistoryDTO> orderHistoryDTO = orderItems.stream()
    //                     .map(orderItem -> new OrderHistoryDTO(orderItem.getOrder().getOrderdate(),
    //                             orderItem.getProduct().getName(), orderItem.getQuantity(), orderItem.getPrice()))
    //                     .collect(Collectors.toList());

    //             return new ResponseEntity<>(orderHistoryDTO, HttpStatus.OK);
    //         } else {
    //             return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    //         }
    //     } catch (Exception e) {
    //         return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    //     }
    // }

}


