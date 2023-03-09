package com.restaurant.restaurantbackend.security.role_based_auth.service;

import com.restaurant.restaurantbackend.security.role_based_auth.dao.RoleDao;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleDao roleDao;

    public Role createNewRole(Role role) {
        return roleDao.save(role);
    }
}
