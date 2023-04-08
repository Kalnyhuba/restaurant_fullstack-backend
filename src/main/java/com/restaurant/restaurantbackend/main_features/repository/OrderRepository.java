package com.restaurant.restaurantbackend.main_features.repository;

import com.restaurant.restaurantbackend.main_features.entity.OrderDetails;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderDetails, Integer> {
    public List<OrderDetails> findByUser(User user);
}
