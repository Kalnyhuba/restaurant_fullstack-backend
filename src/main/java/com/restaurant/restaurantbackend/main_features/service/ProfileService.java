package com.restaurant.restaurantbackend.main_features.service;

import com.restaurant.restaurantbackend.main_features.dto.ProfileDto;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import com.restaurant.restaurantbackend.security.role_based_auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final UserService userService;

    @Autowired
    public ProfileService(UserService userService) {
        this.userService = userService;
    }

    public ProfileDto getUserProfileData() {
        User user = userService.getCurrentUser();
        return new ProfileDto(
                user.getFirstName() + " " + user.getLastName(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
