package com.restaurant.restaurantbackend.admin.controller;

import com.restaurant.restaurantbackend.admin.entity.Product;
import com.restaurant.restaurantbackend.admin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping({"/admin/add-product"})
    public Product addNewProduct(@RequestBody Product product) {
        return productService.addNewProduct(product);
    }
}
