package com.restaurant.restaurantbackend.main_features.controller;

import com.restaurant.restaurantbackend.main_features.dto.ProfileDto;
import com.restaurant.restaurantbackend.main_features.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping({"/user-profile"})
    public ProfileDto getUserProfileData() {
        return profileService.getUserProfileData();
    }
}
