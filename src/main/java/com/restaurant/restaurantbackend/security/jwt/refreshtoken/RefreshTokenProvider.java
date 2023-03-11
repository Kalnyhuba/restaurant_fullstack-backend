package com.restaurant.restaurantbackend.security.jwt.refreshtoken;

import com.restaurant.restaurantbackend.exception.CustomException;
import com.restaurant.restaurantbackend.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Component
public class RefreshTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtUtil jwtUtil;

    private final long refreshTokenValidity;

    private final long refreshTokenAbsoluteValidity;

    @Autowired
    public RefreshTokenProvider(RefreshTokenRepository refreshTokenRepository, JwtUtil jwtUtil, Environment environment) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
        refreshTokenValidity = Objects.requireNonNull(environment.getProperty("jwt.refresh.sliding.validity", Long.class)) * 1000;
        refreshTokenAbsoluteValidity = Objects.requireNonNull(environment.getProperty("jwt.refresh.absolute.validity", Long.class)) * 1000;
    }

    public long getRefreshTokenAbsoluteValidity() {
        return refreshTokenAbsoluteValidity;
    }

    public String createToken(String oldToken, String username, String type, Collection<? extends GrantedAuthority> roles,
                              long loginId, Date absoluteExpirationDate) {
        if (!oldToken.isEmpty()) {
            refreshTokenRepository.deleteByToken(oldToken);
        }
        String token = jwtUtil.createToken(username, type, roles, loginId, absoluteExpirationDate, refreshTokenValidity);
        Long expiresAt = System.currentTimeMillis() + refreshTokenValidity;
        RefreshToken refreshToken = new RefreshToken(username, loginId, token, expiresAt);
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public String updateToken(String oldToken, String username, String type, Collection<? extends GrantedAuthority> roles,
                              long loginId, Date absoluteExpirationDate) {
        String newToken = jwtUtil.createToken(username, type, roles, loginId, absoluteExpirationDate, refreshTokenValidity);
        Long expiresAt = System.currentTimeMillis() + refreshTokenValidity;
        refreshTokenRepository.updateTokenAndExpiresAt(oldToken, newToken, expiresAt);
        return newToken;
    }

    public String validateToken(String token) {
        if (!jwtUtil.getTypeFromToken(token).equals("refresh-token")) {
            throw new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(token);
        if (refreshTokenOptional.isEmpty()) {
            throw new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }
        RefreshToken refreshToken = refreshTokenOptional.get();
        Long expiresAt = refreshToken.getExpiresAt();
        if (System.currentTimeMillis() > expiresAt) {
            refreshTokenRepository.deleteByToken(token);
            throw new CustomException("Expired refresh token", HttpStatus.UNAUTHORIZED);
        }
        return refreshToken.getUsername();
    }

    public void invalidateUserTokenWithLoginId(String username, String token) {
        long loginId = getLoginIdFromToken(token);
        refreshTokenRepository.deleteByUserAndLoginId(username, loginId);
    }

    public long getLoginIdFromToken(String token) {
        return jwtUtil.getLoginIdentifierFromToken(token);
    }

    public void invalidateUserTokens(String username) {
        refreshTokenRepository.deleteByUser(username);
    }

    public void invalidateAllExpiredTokens() {
        refreshTokenRepository.deleteAllByExpiresAtBefore(System.currentTimeMillis());
    }

    public Date getAbsoluteExpirationDateFromToken(String token) {
        return jwtUtil.getAbsoluteExpirationDateFromToken(token);
    }

    public void authorize(String refreshToken) {
        jwtUtil.authorize(refreshToken);
    }

    public void removeCookies(HttpServletResponse response) {
        jwtUtil.removeCookies(response);
    }

    public ResponseCookie setCookie(String name, String value, long age, boolean httpOnly, String path) {
        return jwtUtil.setCookie(name, value, age, httpOnly, path);
    }
}
