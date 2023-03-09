package com.restaurant.restaurantbackend.security.role_based_auth.dao;

import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<User, String> {
}
