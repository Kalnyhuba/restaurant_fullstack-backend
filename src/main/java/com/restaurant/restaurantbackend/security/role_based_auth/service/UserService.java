package com.restaurant.restaurantbackend.security.role_based_auth.service;

import com.restaurant.restaurantbackend.security.role_based_auth.dao.RoleDao;
import com.restaurant.restaurantbackend.security.role_based_auth.dao.UserDao;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.Role;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerNewUser(User user) {
        Role role = roleDao.findById("User").get();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setPassword(getEncodedPassword(user.getPassword()));
        return userDao.save(user);
    }

    public void initRolesAndUser() {
        Role adminRole = new Role();
        adminRole.setRole("Admin");
        adminRole.setDescription("Admin role");
        roleDao.save(adminRole);

        Role userRole = new Role();
        userRole.setRole("User");
        userRole.setDescription("Role for new and existing users");
        roleDao.save(userRole);

        User adminUser = new User();
        adminUser.setFirstName("System");
        adminUser.setLastName("Administrator");
        adminUser.setUsername("sysadmin");
        adminUser.setPassword(getEncodedPassword("admin123"));
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRoles(adminRoles);
        userDao.save(adminUser);

        /*User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("testuser1");
        user.setPassword(getEncodedPassword("test123"));
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        user.setRoles(userRoles);
        userDao.save(user);*/
    }

    public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
