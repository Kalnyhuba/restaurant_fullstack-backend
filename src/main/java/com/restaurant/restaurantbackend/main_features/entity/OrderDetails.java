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
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String fullAddress;

    @Column(nullable = false)
    private String contactNumber;

    @Column(nullable = false)
    private String orderStatus;

    @Column(nullable = false)
    private Double amount;

    @OneToOne
    private Product product;

    @OneToOne
    private User user;

    public OrderDetails(String fullName, String fullAddress, String contactNumber, String orderStatus, Double amount, Product product, User user) {
        this.fullName = fullName;
        this.fullAddress = fullAddress;
        this.contactNumber = contactNumber;
        this.orderStatus = orderStatus;
        this.amount = amount;
        this.product = product;
        this.user = user;
    }
}
