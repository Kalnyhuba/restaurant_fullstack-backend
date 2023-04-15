package com.restaurant.restaurantbackend.main_features.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderDetailsItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne
    private OrderDetails orderDetails;

    @ManyToOne
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    public OrderDetailsItem(OrderDetails orderDetails, Product product, Integer quantity) {
        this.orderDetails = orderDetails;
        this.product = product;
        this.quantity = quantity;
    }
}
