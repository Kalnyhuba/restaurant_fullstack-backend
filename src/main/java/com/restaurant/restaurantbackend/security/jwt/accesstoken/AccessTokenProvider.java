package com.restaurant.restaurantbackend.security.jwt.accesstoken;

import com.restaurant.restaurantbackend.exception.CustomException;
import com.restaurant.restaurantbackend.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

@Component
public class AccessTokenProvider {

    private final JwtUtil jwtUtil;

    private final long accessTokenValidityInMilliseconds;

    @Autowired
    public AccessTokenProvider(JwtUtil jwtUtil, Environment environment) {
        this.jwtUtil = jwtUtil;
        accessTokenValidityInMilliseconds = Objects.requireNonNull(environment.getProperty("jwt.access.validity", Long.class)) * 1000;
    }

    public String createToken(String username, String type, Collection<? extends GrantedAuthority> roles) {
        return jwtUtil.createToken(username, type, roles, accessTokenValidityInMilliseconds);
    }

    public void validateToken(String token) {
        if (!jwtUtil.getTypeFromToken(token).equals("access-token")) {
            throw new CustomException("Invalid access token", HttpStatus.UNAUTHORIZED);
        }
    }

    public void authorize(String accessToken) {
        jwtUtil.authorize(accessToken);
    }
}