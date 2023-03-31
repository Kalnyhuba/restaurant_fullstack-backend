package com.restaurant.restaurantbackend.admin.service;

import com.restaurant.restaurantbackend.admin.entity.Product;
import com.restaurant.restaurantbackend.admin.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product addNewProduct(Product product) {
        return productRepository.save(product);
    }
}
