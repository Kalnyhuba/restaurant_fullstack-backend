package com.restaurant.restaurantbackend.security.role_based_auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ResetDto {

    @NotNull(message = "Token is not set")
    private String token;

    @NotNull(message = "Password is not set")
    private String password;
}
