package com.restaurant.restaurantbackend.security.role_based_auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.restaurantbackend.exception.CustomException;
import com.restaurant.restaurantbackend.security.response.ResponseMessage;
import com.restaurant.restaurantbackend.security.role_based_auth.dto.LoginDto;
import com.restaurant.restaurantbackend.security.role_based_auth.dto.RegisterDto;
import com.restaurant.restaurantbackend.security.role_based_auth.dto.ResetDto;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import com.restaurant.restaurantbackend.security.role_based_auth.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    @Value("${jwt.access.validity}")
    private long accessCookieAge;

    @Value("${jwt.refresh.absolute.validity}")
    private long refreshCookieAge;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/user/auth/login")
    public ResponseMessage login(@Valid @RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getCookies() != null ? Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refresh-token"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("") : "";
        Map<String, String> values = userService.signIn(loginDto.getUsername(), loginDto.getPassword(), refreshToken);
        ResponseCookie accessCookie = userService.setCookie("access-token", values.get("accessToken"), accessCookieAge, true, "/");
        ResponseCookie refreshCookie = userService.setCookie("refresh-token", values.get("refreshToken"), refreshCookieAge, true, "/user/auth");
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return new ResponseMessage(values.get("authorities"));
    }

    @PostMapping("/user/register")
    public ResponseMessage register(@Valid @RequestBody RegisterDto registerDto) {
        return new ResponseMessage(userService.signUp(modelMapper.map(registerDto, User.class)));
    }

    @PostMapping("/user/verify")
    public ResponseMessage verifyEmail(@RequestBody String token) {
        return new ResponseMessage(userService.verifyEmail(token));
    }

    @GetMapping ("/user/current_username")
    public ResponseMessage getCurrentUsername() {
        return new ResponseMessage(UserService.getCurrentUsername());
    }

    @GetMapping("/user/current_user_authorities")
    public ResponseMessage getCurrentUserAuthorities() {
        try {
            return new ResponseMessage(objectMapper.writeValueAsString(userService.getCurrentUserAuthorities()));
        } catch (JsonProcessingException e) {
            throw new CustomException("Could not retrieve user authorities", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user/auth/refresh")
    public ResponseMessage refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("refresh-token"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElseThrow(() -> new CustomException("Expired refresh cookie", HttpStatus.UNAUTHORIZED));
            Map<String, String> values = userService.refresh(refreshToken);
            ResponseCookie newAccessCookie = userService.setCookie("access-token", values.get("accessToken"), accessCookieAge, true, "/");
            ResponseCookie newRefreshCookie = userService.setCookie("refresh-token", values.get("refreshToken"), refreshCookieAge, true, "/user/auth");
            response.addHeader(HttpHeaders.SET_COOKIE, newAccessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, newRefreshCookie.toString());
            return new ResponseMessage(values.get("authorities"));
        } catch (CustomException e) {
            userService.removeCookies(response);
            throw e;
        }
    }

    @GetMapping("/user/auth/logout")
    public ResponseMessage logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = request.getCookies() != null ? Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("refresh-token"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse("") : "";
            String responseString = userService.signOut(refreshToken);
            userService.removeCookies(response);
            return new ResponseMessage(responseString);
        } catch (CustomException e) {
            userService.removeCookies(response);
            throw e;
        }
    }

    @PostMapping("/user/forgot_password")
    public ResponseMessage sendPasswordResetLink(@RequestBody String email) {
        return new ResponseMessage(userService.recoverPassword(email));
    }

    @PostMapping("/user/reset_password")
    public ResponseMessage changePassword(@Valid @RequestBody ResetDto resetDto) {
        return new ResponseMessage(userService.resetPassword(resetDto.getToken(), resetDto.getPassword()));
    }
}
