package com.restaurant.restaurantbackend.main_features.controller;

import com.restaurant.restaurantbackend.main_features.entity.Image;
import com.restaurant.restaurantbackend.main_features.entity.Product;
import com.restaurant.restaurantbackend.main_features.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = {"/admin/add-product"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Product addNewProduct(@RequestPart("product") Product product,
                                 @RequestPart("image") MultipartFile[] file) {
        try {
            Set<Image> images = uploadImage(file);
            product.setImages(images);
            return productService.addNewProduct(product);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Set<Image> uploadImage(MultipartFile[] multipartFiles) throws IOException {
        Set<Image> imageSet = new HashSet<>();
        for (MultipartFile file: multipartFiles) {
            Image image = new Image(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
            imageSet.add(image);
        }
        return imageSet;
    }

    @GetMapping({"/admin/all-products"})
    public List<Product> getAllProductsAdmin(@RequestParam(defaultValue = "") String searchKey) {
        return productService.getAllProducts(searchKey);
    }

    @GetMapping({"/get-product-by-id/{id}"})
    public Product getProductById(@PathVariable("id") Integer id) {
        return productService.getProductById(id);
    }

    @DeleteMapping({"/admin/delete-product/{id}"})
    public void deleteProduct(@PathVariable("id") Integer id) {
        productService.deleteProduct(id);
    }

    @GetMapping({"/all-products"})
    public List<Product> noCredentialsGetAllProducts(@RequestParam(defaultValue = "") String searchKey) {
        return productService.getAllProducts(searchKey);
    }

    @GetMapping({"/product-details/{isSingleProduct}/{productId}"})
    public List<Product> getProductDetails(@PathVariable(name = "isSingleProduct") boolean isSingleProduct, @PathVariable(name = "productId") Integer productId) {
        return productService.getProductDetails(isSingleProduct, productId);
    }
}
