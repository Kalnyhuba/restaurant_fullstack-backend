package com.restaurant.restaurantbackend.main_features.dto;

import com.restaurant.restaurantbackend.main_features.entity.Product;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartDto {

    private Product product;

    private User user;
}
