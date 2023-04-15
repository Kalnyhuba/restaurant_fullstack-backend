package com.restaurant.restaurantbackend.main_features.repository;

import com.restaurant.restaurantbackend.main_features.entity.OrderDetailsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailsItemRepository extends JpaRepository<OrderDetailsItem, Integer> {

    List<OrderDetailsItem> findAllByOrderDetailsId(Integer orderDetailsId);

    @Modifying
    void deleteAllByProductId(Integer productId);

}
