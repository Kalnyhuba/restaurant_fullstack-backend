package com.restaurant.restaurantbackend.main_features.service;

import com.restaurant.restaurantbackend.main_features.entity.Cart;
import com.restaurant.restaurantbackend.main_features.entity.Product;
import com.restaurant.restaurantbackend.main_features.repository.CartRepository;
import com.restaurant.restaurantbackend.main_features.repository.ProductRepository;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import com.restaurant.restaurantbackend.security.role_based_auth.repository.UserRepository;
import com.restaurant.restaurantbackend.security.role_based_auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    public Product addNewProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts(String searchKey) {
        if (searchKey.equals("")) {
            return productRepository.findAll();
        } else {
            return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchKey, searchKey);
        }
    }

    public List<Product> getProductDetails(boolean isSingleProduct, Integer productId) {
        if (isSingleProduct && productId != 0) {
            List<Product> products = new ArrayList<>();
            Product product = productRepository.findById(productId).get();
            products.add(product);
            return products;
        } else {
            String username = UserService.getCurrentUsername();
            User user = userRepository.findByUsername(username).get();
            List<Cart> carts = cartRepository.findByUser(user);
            return carts.stream().map(Cart::getProduct).collect(Collectors.toList());
        }
    }

    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).get();
    }
}
