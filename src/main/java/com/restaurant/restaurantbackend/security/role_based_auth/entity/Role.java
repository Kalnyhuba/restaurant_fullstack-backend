package com.restaurant.restaurantbackend.security.role_based_auth.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Role {
    @Id
    @Column(nullable = false)
    private String role;

    private String description;
}
