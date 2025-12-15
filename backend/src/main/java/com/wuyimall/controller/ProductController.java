package com.wuyimall.controller;

import com.wuyimall.entity.Product;
import com.wuyimall.entity.CartItem;
import com.wuyimall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> list(@RequestParam(required = false) Long categoryId) {
        return productService.listByCategory(categoryId);
    }

    @GetMapping("/{id}")
    public Product detail(@PathVariable Long id) {
        return productService.getById(id);
    }
}