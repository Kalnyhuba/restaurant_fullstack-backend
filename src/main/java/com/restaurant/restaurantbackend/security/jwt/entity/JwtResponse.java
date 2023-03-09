package com.restaurant.restaurantbackend.security.jwt.entity;

import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {

    private User user;
    private String jwtToken;

    public JwtResponse(User user, String jwtToken) {
        this.user = user;
        this.jwtToken = jwtToken;
    }
}
