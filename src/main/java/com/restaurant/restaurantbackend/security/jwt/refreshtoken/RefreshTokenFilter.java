package com.restaurant.restaurantbackend.security.jwt.refreshtoken;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.restaurantbackend.exception.CustomException;
import com.restaurant.restaurantbackend.security.role_based_auth.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class RefreshTokenFilter extends OncePerRequestFilter {

    private static final String filteredEndPoint = "/user/auth/refresh";

    private final UserRepository userRepository;

    private final RefreshTokenProvider refreshTokenProvider;

    private final ObjectMapper objectMapper;

    private final AntPathMatcher antPathMatcher;

    public RefreshTokenFilter(UserRepository userRepository, RefreshTokenProvider refreshTokenProvider, ObjectMapper objectMapper, AntPathMatcher antPathMatcher) {
        this.userRepository = userRepository;
        this.refreshTokenProvider = refreshTokenProvider;
        this.objectMapper = objectMapper;
        this.antPathMatcher = antPathMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (request.getCookies() == null) {
                throw new CustomException("Expired refresh cookie", HttpStatus.UNAUTHORIZED);
            }
            Optional<String> refreshCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("refresh-token"))
                    .map(Cookie::getValue).findFirst();
            if (refreshCookie.isEmpty()) {
                throw new CustomException("Expired refresh cookie", HttpStatus.UNAUTHORIZED);
            }
            try {
                String refreshToken = refreshCookie.get();
                String username = refreshTokenProvider.validateToken(refreshToken);
                if (!userRepository.existsByUsername(username)) {
                    refreshTokenProvider.invalidateUserTokens(username);
                    throw new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
                }
                refreshTokenProvider.authorize(refreshToken);
                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException e) {
                throw new CustomException("Expired refresh token", HttpStatus.UNAUTHORIZED);
            } catch (JwtException | IllegalArgumentException e) {
                throw new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
            } catch (DataAccessException e) {
                throw new CustomException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (CustomException e) {
            SecurityContextHolder.clearContext();
            refreshTokenProvider.removeCookies(response);
            response.setStatus(e.getHttpStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(e.getMessage()));
        }
    }

    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !antPathMatcher.match(filteredEndPoint, request.getRequestURI());
    }
}
