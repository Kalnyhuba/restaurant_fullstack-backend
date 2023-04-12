package com.restaurant.restaurantbackend.main_features.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private String fullName;

    private String username;

    private String email;
}
