package com.restaurant.restaurantbackend.security.role_based_auth.dto;

import com.restaurant.restaurantbackend.security.role_based_auth.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private String username;

    private String email;

    List<Role> roles;
}
