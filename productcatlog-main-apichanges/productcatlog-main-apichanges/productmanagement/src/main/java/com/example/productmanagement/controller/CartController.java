package com.example.productmanagement.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.example.productmanagement.service.ProductService;
import com.example.productmanagement.service.UserService;

@RestController
public class CartController {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;


    @PostMapping("/add-cart/{productId}")
    public ResponseEntity<?> addtocart(@PathVariable Long productId, @RequestParam int quantity,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String credentials = new String(Base64.getDecoder().decode(authHeader.split(" ")[1]));
            String[] splitCredentials = credentials.split(":");
            String email = splitCredentials[0];
            String password = splitCredentials[1];
            User user = userService.findByEmailAndPassword(email, password);
            // System.out.println("user +" + user);
            if (user != null) {
                Product product = productService.getProductById(productId);

                if (product != null) {
                    if (quantity > product.getAvailableStock()) {
                        return new ResponseEntity<>("Requested quantity exceeds available stock",
                                HttpStatus.BAD_REQUEST);
                    }
                    if (quantity > 0) {
                        Optional<CartItem> existingCartItem = cartItemRepository.findByUserAndProduct(user, product);

                        if (existingCartItem.isPresent()) {
                            CartItem cartItem = existingCartItem.get();
                            cartItem.setQuantity(cartItem.getQuantity() + quantity);
                            cartItemRepository.save(cartItem);
                        } else {
                            CartItem cartItem = new CartItem();
                            cartItem.setProduct(product);
                            cartItem.setQuantity(quantity);
                            cartItem.setUser(user);
                            cartItemRepository.save(cartItem);
                        }

                        return new ResponseEntity<>("Product added to the cart successfully", HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("Product quantity less than 0", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>("product with id  not found", HttpStatus.NOT_FOUND);
                }

            } else {
                return new ResponseEntity<>("Invalid credentials or user not found", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cart")
    public ResponseEntity<?> viewcart(@RequestHeader("Authorization") String authHeader) {

        try {
            String credentials = new String(Base64.getDecoder().decode(authHeader.split(" ")[1]));
            String splitCredentials[] = credentials.split(":");
            String email = splitCredentials[0];
            String password = splitCredentials[1];
            User user = userService.findByEmailAndPassword(email, password);
            if (user != null) {
                List<CartItem> cart = user.getCartItem();
                if (cart.isEmpty()) {
                    return new ResponseEntity<>("cart is empty", HttpStatus.OK);
                } else {
                    BigDecimal totalprice = cart.stream()
                            .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    Map<String, Object> response = new HashMap<>();
                    response.put("totalprice", totalprice);
                    response.put("cartitems ", cart);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

            } else {
                return new ResponseEntity<>("Invalid credentials user not found", HttpStatus.UNAUTHORIZED);
            }
        }

        catch (Exception e) {
            return new ResponseEntity<>("error in viewing cart", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestHeader("Authorization") String authHeader,
            @RequestParam("paymentMethodId") int paymentMethodId,
            @RequestParam("shippingAddress") String shippingAddress) {
        try {
            String credentials = new String(Base64.getDecoder().decode(authHeader.split(" ")[1]));
            String splitCredentials[] = credentials.split(":");
            String email = splitCredentials[0];
            String password = splitCredentials[1];
            User user = userService.findByEmailAndPassword(email, password);

            if (user != null) {
                List<CartItem> cartItems = cartItemRepository.findByUser(user);

                if (cartItems.isEmpty()) {
                    return new ResponseEntity<>("Cart is empty", HttpStatus.BAD_REQUEST);
                }

                BigDecimal totalPrice = cartItems.stream()
                        .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                Orders order = new Orders();
                order.setUser(user);
                order.setTotal_price(totalPrice);
                order.setPayment_id(paymentMethodRepository.getById(paymentMethodId));
                order.setShippingAddress(shippingAddress);
                order.setOrderdate(new Timestamp(System.currentTimeMillis()));
                ordersRepository.save(order);

                List<OrderItem> orderItems = new ArrayList<>();
                for (CartItem cartItem : cartItems) {
                    if (cartItem.getQuantity() > cartItem.getProduct().getAvailableStock()) {
                        return new ResponseEntity<>("Quantity is higher than available stock", HttpStatus.BAD_REQUEST);
                    }
                    int remainingStock = cartItem.getProduct().getAvailableStock() - cartItem.getQuantity();
                    cartItem.getProduct().setAvailableStock(remainingStock);
                    productRepository.save(cartItem.getProduct());

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getProduct().getPrice());
                    orderItems.add(orderItem);
                }

                orderItemRepository.saveAll(orderItems);

                cartItemRepository.deleteAll(cartItems);

                return new ResponseEntity<>("Checkout successfully done", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Unauthorized user", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Error during checkout" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

   
}
/*
 * 
 * @GetMapping("/orderhistory")
public ResponseEntity<List<OrderHistoryDTO>> getOrderHistory(@RequestHeader("Authorization") String authHeader) {
    try {
        String credentials = new String(Base64.getDecoder().decode(authHeader.split(" ")[1]));
        String[] splitCredentials = credentials.split(":");
        String email = splitCredentials[0];
        String password = splitCredentials[1];
        User user = userService.findByEmailAndPassword(email, password);

        if (user != null) {
            List<Order> orders = ordersRepository.findByUser(user);
            List<OrderHistoryDTO> orderHistoryList = new ArrayList<>();

            for (Order order : orders) {
                BigDecimal totalAmount = order.getOrderItems().stream()
                        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                OrderHistoryDTO orderHistoryDTO = new OrderHistoryDTO(
                        order.getId(),
                        order.getOrderdate(),
                        totalAmount
                );

                orderHistoryList.add(orderHistoryDTO);
            }

            return new ResponseEntity<>(orderHistoryList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
public class OrderHistoryDTO {
    private Long orderId;
    private Timestamp orderDate;
    private BigDecimal totalAmount;

    public OrderHistoryDTO(Long orderId, Timestamp orderDate, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
    }

    // Getters and setters (or make fields public)
}
 */