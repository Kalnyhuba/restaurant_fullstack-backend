package com.restaurant.restaurantbackend.main_features.entity;

import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @OneToOne
    private Product product;

    @OneToOne
    private User user;

    public CartItem(Product product, User user) {
        this.product = product;
        this.user = user;
    }
}
