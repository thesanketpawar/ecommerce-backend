package com.sanky.ecommerce.controller;

import com.sanky.ecommerce.model.Cart;
import com.sanky.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public Cart getCart(Authentication authentication) {
        return cartService.getOrCreateCart(authentication.getName());
    }

    @PostMapping("/add")
    public Cart addItem(Authentication authentication,
                         @RequestParam Long productId,
                         @RequestParam Integer quantity) {
        return cartService.addItem(authentication.getName(), productId, quantity);
    }

    @DeleteMapping("/item/{cartItemId}")
    public void removeItem(@PathVariable Long cartItemId) {
        cartService.removeItem(cartItemId);
    }
}
