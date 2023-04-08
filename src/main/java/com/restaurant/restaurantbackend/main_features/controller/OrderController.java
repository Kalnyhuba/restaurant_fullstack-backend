package com.restaurant.restaurantbackend.main_features.controller;

import com.restaurant.restaurantbackend.main_features.entity.OrderDetails;
import com.restaurant.restaurantbackend.main_features.entity.OrderInput;
import com.restaurant.restaurantbackend.main_features.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping({"/place-order/{isSingleProduct}"})
    public void placeOrder(@PathVariable(name = "isSingleProduct") boolean isSingleProduct, @RequestBody OrderInput orderInput) {
        orderService.placeOrder(orderInput, isSingleProduct);
    }

    @GetMapping({"/get-order-details"})
    public List<OrderDetails> getOrderDetails() {
        return orderService.getOrderDetails();
    }
}
