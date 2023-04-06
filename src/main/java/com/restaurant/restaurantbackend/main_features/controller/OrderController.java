package com.restaurant.restaurantbackend.main_features.controller;

import com.restaurant.restaurantbackend.main_features.entity.OrderInput;
import com.restaurant.restaurantbackend.main_features.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping({"/place-order"})
    public void placeOrder(@RequestBody OrderInput orderInput) {
        orderService.placeOrder(orderInput);
    }
}
