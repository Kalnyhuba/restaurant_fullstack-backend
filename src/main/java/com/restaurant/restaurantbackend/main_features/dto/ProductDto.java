package com.restaurant.restaurantbackend.main_features.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDto {

    private String name;

    private String description;

    private Double price;
}
