package com.restaurant.restaurantbackend.main_features.controller;

import com.restaurant.restaurantbackend.main_features.entity.Cart;
import com.restaurant.restaurantbackend.main_features.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping({"/add-to-cart/{id}"})
    public Cart addToCart(@PathVariable(name = "id") Integer id) {
        return cartService.addToCart(id);
    }

    @GetMapping({"/cart-details"})
    public List<Cart> getCartDetails() {
        return cartService.getCartDetails();
    }

    @DeleteMapping({"/delete-cart-item/{cartItemId}"})
    public void deleteCartItem(@PathVariable(name = "cartItemId") Integer cartItemId) {
        cartService.deleteCartItem(cartItemId);
    }
}
