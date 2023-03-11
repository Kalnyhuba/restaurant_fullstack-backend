package com.restaurant.restaurantbackend.security.role_based_auth.service;

import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

public interface UserService {

    Map<String, String> signIn(String username, String password, String refreshToken);

    String signUp(User user);

    Map<String, String> refresh(String refreshToken);

    String signOut(String refreshToken);

    String getCurrentUsername();

    Collection<? extends GrantedAuthority> getCurrentUserAuthorities();

    Integer getCurrentUserId();

    User getUserByUsername(String username);

    User getUserByUsernameOrEmail(String word);

    User getCurrentUser();

    void saveUser(User user);

    String recoverPassword(String email);

    String resetPassword(String token, String password);

    String verifyEmail(String token);

    void removeCookies(HttpServletResponse response);

    ResponseCookie setCookie(String name, String value, long age, boolean httpOnly, String path);
}
