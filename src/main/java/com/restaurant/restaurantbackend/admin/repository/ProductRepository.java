package com.restaurant.restaurantbackend.admin.repository;

import com.restaurant.restaurantbackend.admin.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
