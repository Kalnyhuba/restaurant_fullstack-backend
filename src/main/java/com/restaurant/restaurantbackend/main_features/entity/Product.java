package com.restaurant.restaurantbackend.main_features.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double price;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "product_images", joinColumns = {
            @JoinColumn(name = "id")
    },
            inverseJoinColumns = {
                    @JoinColumn(name = "image_id")
            })
    private Set<Image> images;

    public Product(String name, String description, Double price, Set<Image> images) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.images = images;
    }
}
