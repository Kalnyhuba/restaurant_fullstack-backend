package com.restaurant.restaurantbackend.security.role_based_auth.dao;

import com.restaurant.restaurantbackend.security.role_based_auth.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao extends CrudRepository<Role, String> {
}
