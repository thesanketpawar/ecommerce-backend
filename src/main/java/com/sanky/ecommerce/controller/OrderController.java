package com.sanky.ecommerce.controller;

import com.sanky.ecommerce.model.Order;
import com.sanky.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public Order checkout(Authentication authentication) {
        return orderService.checkout(authentication.getName());
    }

    @GetMapping
    public List<Order> getMyOrders(Authentication authentication) {
        return orderService.getUserOrders(authentication.getName());
    }
}
