package com.restaurant.restaurantbackend.main_features.service;

import com.restaurant.restaurantbackend.main_features.entity.CartItem;
import com.restaurant.restaurantbackend.main_features.entity.Product;
import com.restaurant.restaurantbackend.main_features.repository.CartRepository;
import com.restaurant.restaurantbackend.main_features.repository.ProductRepository;
import com.restaurant.restaurantbackend.security.role_based_auth.entity.User;
import com.restaurant.restaurantbackend.security.role_based_auth.repository.UserRepository;
import com.restaurant.restaurantbackend.security.role_based_auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public CartItem addToCart(Integer id) {
        Product product = productRepository.findById(id).get();
        String username = UserService.getCurrentUsername();
        User user = null;
        if (username != null) {
            user = userRepository.findByUsername(username).get();
        }
        List<CartItem> cartItemList = cartRepository.findByUser(user);
        List<CartItem> cartItemFiltered = cartItemList.stream().filter(c -> c.getProduct().getId() == id).collect(Collectors.toList());
        if (cartItemFiltered.size() > 0) {
            return null;
        }
        if (user != null) {
            CartItem cartItem = new CartItem(product, user);
            return cartRepository.save(cartItem);
        }
        return null;
    }

    public List<CartItem> getCartDetails() {
        String username = UserService.getCurrentUsername();
        User user = userRepository.findByUsername(username).get();
        return cartRepository.findByUser(user);
    }

    public void deleteCartItem(Integer cartItemId) {
        cartRepository.deleteById(cartItemId);
    }
}
