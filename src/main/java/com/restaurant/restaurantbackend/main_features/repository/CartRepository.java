package com.restaurant.restaurantbackend.main_features.repository;

import com.restaurant.restaurantbackend.main_features.entity.Cart;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    public List<Cart> findByUser(User user);
}
