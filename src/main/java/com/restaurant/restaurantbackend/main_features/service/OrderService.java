package com.restaurant.restaurantbackend.main_features.service;

import com.restaurant.restaurantbackend.main_features.entity.OrderDetails;
import com.restaurant.restaurantbackend.main_features.entity.OrderInput;
import com.restaurant.restaurantbackend.main_features.entity.OrderProductQuantity;
import com.restaurant.restaurantbackend.main_features.entity.Product;
import com.restaurant.restaurantbackend.main_features.repository.OrderRepository;
import com.restaurant.restaurantbackend.main_features.repository.ProductRepository;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import com.restaurant.restaurantbackend.security.role_based_auth.repository.UserRepository;
import com.restaurant.restaurantbackend.security.role_based_auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private static final String ORDER_PLACED = "Rendelés leadva";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private UserRepository userRepository;

    public void placeOrder(OrderInput orderInput) {
        List<OrderProductQuantity> orderProductQuantityList = orderInput.getOrderProductQuantityList();
        for (OrderProductQuantity o : orderProductQuantityList) {
            Product product = productRepository.findById(o.getProductId()).get();
            String username = UserService.getCurrentUsername();
            User user = userRepository.findByUsername(username).get();
            OrderDetails order = new OrderDetails(
                    orderInput.getFullName(),
                    orderInput.getFullAddress(),
                    orderInput.getContactNumber(),
                    ORDER_PLACED,
                    product.getPrice() * o.getQuantity(),
                    product,
                    user
            );
            orderRepository.save(order);
        }
    }
}
