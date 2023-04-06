package com.restaurant.restaurantbackend.main_features.repository;

import com.restaurant.restaurantbackend.main_features.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderDetails, Integer> {
}
