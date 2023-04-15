package com.restaurant.restaurantbackend.main_features.dto;

import com.restaurant.restaurantbackend.main_features.entity.OrderDetailsItem;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsResponseDto {

    private Integer id;

    private String fullName;

    private String fullAddress;

    private String contactNumber;

    private String orderStatus;

    private Double amount;

    private List<OrderDetailsItem> items;

    private User user;

}
