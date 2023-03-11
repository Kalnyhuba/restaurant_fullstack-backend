package com.restaurant.restaurantbackend.security.role_based_auth.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ADMIN,
    CLIENT;

    @Override
    public String getAuthority() {
        return name();
    }
}
