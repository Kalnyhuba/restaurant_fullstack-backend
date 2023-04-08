package com.restaurant.restaurantbackend.security.jwt.accesstoken;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.restaurantbackend.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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

public class AccessTokenFilter extends OncePerRequestFilter {

    private static final String[] excludedEndPoints = new String[]{
            "/user/auth/login",
            "/user/register",
            "/user/forgot_password",
            "/user/reset_password",
            "/user/verify",
            "/user/auth/refresh",
            "/all-products"
    };

    private final AccessTokenProvider accessTokenProvider;

    private final ObjectMapper objectMapper;

    private final AntPathMatcher antPathMatcher;

    public AccessTokenFilter(AccessTokenProvider accessTokenProvider, ObjectMapper objectMapper, AntPathMatcher antPathMatcher) {
        this.accessTokenProvider = accessTokenProvider;
        this.objectMapper = objectMapper;
        this.antPathMatcher = antPathMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            if (httpServletRequest.getCookies() == null) {
                throw new CustomException("Expired access cookie", HttpStatus.UNAUTHORIZED);
            }
            Optional<String> accessCookie = Arrays.stream(httpServletRequest.getCookies()).filter(cookie -> cookie.getName().equals("access-token"))
                    .map(Cookie::getValue).findFirst();
            if (accessCookie.isEmpty()) {
                throw new CustomException("Expired access cookie", HttpStatus.UNAUTHORIZED);
            }
            try {
                String accessToken = accessCookie.get();
                accessTokenProvider.validateToken(accessToken);
                accessTokenProvider.authorize(accessToken);
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } catch (ExpiredJwtException e) {
                throw new CustomException("Expired access token", HttpStatus.UNAUTHORIZED);
            } catch (JwtException | IllegalArgumentException e) {
                throw new CustomException("Invalid access token", HttpStatus.UNAUTHORIZED);
            }
        } catch (CustomException e) {
            SecurityContextHolder.clearContext();
            httpServletResponse.setStatus(e.getHttpStatus().value());
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(e.getMessage()));
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return Arrays.stream(excludedEndPoints).anyMatch(e -> antPathMatcher.match(e, request.getRequestURI()));
    }
}
