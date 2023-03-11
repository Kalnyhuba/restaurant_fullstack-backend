package com.restaurant.restaurantbackend.security.role_based_auth.repository;

import com.restaurant.restaurantbackend.security.role_based_auth.entity.Role;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByRoles(@Param("roles") List<Role> roles);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
