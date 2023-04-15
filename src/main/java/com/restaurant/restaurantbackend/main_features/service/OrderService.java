package com.restaurant.restaurantbackend.main_features.service;

import com.restaurant.restaurantbackend.main_features.dto.OrderDetailsResponseDto;
import com.restaurant.restaurantbackend.main_features.entity.*;
import com.restaurant.restaurantbackend.main_features.repository.CartRepository;
import com.restaurant.restaurantbackend.main_features.repository.OrderDetailsItemRepository;
import com.restaurant.restaurantbackend.main_features.repository.OrderRepository;
import com.restaurant.restaurantbackend.main_features.repository.ProductRepository;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import com.restaurant.restaurantbackend.security.role_based_auth.repository.UserRepository;
import com.restaurant.restaurantbackend.security.role_based_auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final String ORDER_PLACED = "Rendelés leadva";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderDetailsItemRepository orderDetailsItemRepository;

    public void placeOrder(OrderInput orderInput, boolean isSingleProduct) {
        User user = userRepository.findByUsername(UserService.getCurrentUsername()).orElseThrow();
        double totalAmount = 0;
        OrderDetails order = new OrderDetails(
                orderInput.getFullName(),
                orderInput.getFullAddress(),
                orderInput.getContactNumber(),
                ORDER_PLACED,
                totalAmount,
                user
        );
        List<OrderProductQuantity> orderProductQuantityList = orderInput.getOrderProductQuantityList();
        List<OrderDetailsItem> orderItems = new ArrayList<>();
        for (OrderProductQuantity orderProductQuantity : orderProductQuantityList) {
            Product product = productRepository.findById(orderProductQuantity.getProductId()).orElseThrow();
            totalAmount += product.getPrice() * orderProductQuantity.getQuantity();
            orderItems.add(new OrderDetailsItem(order, product, orderProductQuantity.getQuantity()));
        }
        order.setAmount(totalAmount);
        if (!isSingleProduct) {
            List<CartItem> cartItems = cartRepository.findByUser(user);
            cartItems.forEach(c -> cartRepository.deleteById(c.getId()));
        }
        orderRepository.save(order);
        orderDetailsItemRepository.saveAll(orderItems);
    }

    public List<OrderDetailsResponseDto> getOrderDetails() {
        String username = UserService.getCurrentUsername();
        User user = userRepository.findByUsername(username).get();
        return orderRepository.findByUser(user).stream()
                .map(orderDetails -> {
                    List<OrderDetailsItem> orderDetailsItems = orderDetailsItemRepository.findAllByOrderDetailsId(orderDetails.getId());
                    return new OrderDetailsResponseDto(
                            orderDetails.getId(),
                            orderDetails.getFullName(),
                            orderDetails.getFullAddress(),
                            orderDetails.getContactNumber(),
                            orderDetails.getOrderStatus(),
                            orderDetails.getAmount(),
                            orderDetailsItems,
                            orderDetails.getUser()
                    );
                })
                .collect(Collectors.toList());
    }

    public void deleteAllByProductId(Integer productId) {
        orderDetailsItemRepository.deleteAllByProductId(productId);
    }

}
