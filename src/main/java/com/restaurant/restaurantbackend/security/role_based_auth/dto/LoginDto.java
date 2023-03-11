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
public class LoginDto {

    @NotNull(message = "Username is not set")
    private String username;

    @NotNull(message = "Password is not set")
    private String password;
}
