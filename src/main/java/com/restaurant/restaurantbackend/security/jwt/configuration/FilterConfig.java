package com.restaurant.restaurantbackend.security.jwt.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.restaurantbackend.security.jwt.accesstoken.AccessTokenFilter;
import com.restaurant.restaurantbackend.security.jwt.accesstoken.AccessTokenProvider;
import com.restaurant.restaurantbackend.security.jwt.refreshtoken.RefreshTokenFilter;
import com.restaurant.restaurantbackend.security.jwt.refreshtoken.RefreshTokenProvider;
import com.restaurant.restaurantbackend.security.role_based_auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
public class FilterConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final UserRepository userRepository;

    private final AccessTokenProvider accessTokenProvider;

    private final RefreshTokenProvider refreshTokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    public FilterConfig(UserRepository userRepository, AccessTokenProvider accessTokenProvider, RefreshTokenProvider refreshTokenProvider) {
        this.userRepository = userRepository;
        this.accessTokenProvider = accessTokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        PermissionFilterConfig permissionFilterConfig = new PermissionFilterConfig(objectMapper, antPathMatcher);
        RefreshTokenFilter refreshTokenFilter = new RefreshTokenFilter(userRepository, refreshTokenProvider, objectMapper, antPathMatcher);
        AccessTokenFilter accessTokenFilter = new AccessTokenFilter(accessTokenProvider, objectMapper, antPathMatcher);
        httpSecurity.addFilterBefore(permissionFilterConfig, FilterSecurityInterceptor.class);
        httpSecurity.addFilterBefore(refreshTokenFilter, PermissionFilterConfig.class);
        httpSecurity.addFilterBefore(accessTokenFilter, RefreshTokenFilter.class);
    }
}
