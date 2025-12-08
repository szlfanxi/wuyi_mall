package com.wuyimall.service.impl;

import com.wuyimall.entity.Product;
import com.wuyimall.repository.ProductRepository;
import com.wuyimall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public List<Product> listByCategory(Long categoryId) {
        if (categoryId == null) {
            return productRepository.findAll();
        }
        return productRepository.findByCategoryId(categoryId);
    }
    
    @Override
    public Product getById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}