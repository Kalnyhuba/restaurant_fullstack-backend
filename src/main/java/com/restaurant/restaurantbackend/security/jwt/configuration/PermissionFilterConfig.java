package com.restaurant.restaurantbackend.security.jwt.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.restaurantbackend.exception.CustomException;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PermissionFilterConfig extends OncePerRequestFilter {

    private static final String filteredEndPoint = "/admin/**";

    private final ObjectMapper objectMapper;

    private final AntPathMatcher antPathMatcher;

    public PermissionFilterConfig(ObjectMapper objectMapper, AntPathMatcher antPathMatcher) {
        this.objectMapper = objectMapper;
        this.antPathMatcher = antPathMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                if (authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(Role.ADMIN.getAuthority()))) {
                    filterChain.doFilter(request, response);
                } else {
                    throw new CustomException("Not enough permissions", HttpStatus.UNAUTHORIZED);
                }
            } else {
                throw new CustomException("Not logged in", HttpStatus.UNAUTHORIZED);
            }
        } catch (CustomException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(e.getHttpStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(e.getMessage()));
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !antPathMatcher.match(filteredEndPoint, request.getRequestURI());
    }
}
