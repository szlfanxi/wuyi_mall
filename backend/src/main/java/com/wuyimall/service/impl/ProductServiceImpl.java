package com.wuyimall.service.impl;

import com.wuyimall.entity.Product;
import com.wuyimall.entity.ProductImage;
import com.wuyimall.repository.ProductRepository;
import com.wuyimall.repository.ProductImageRepository;
import com.wuyimall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductImageRepository productImageRepository;
    
    @Override
    public List<Product> listByCategory(Long categoryId) {
        List<Product> products;
        if (categoryId == null) {
            products = productRepository.findAll();
        } else {
            products = productRepository.findByCategoryId(categoryId);
        }
        
        // 为每个商品加载关联的图片
        for (Product product : products) {
            List<ProductImage> images = productImageRepository.findByProductId(product.getId());
            product.setImages(images);
        }
        
        return products;
    }
    
    @Override
    public Product getById(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            // 加载关联的图片
            List<ProductImage> images = productImageRepository.findByProductId(product.getId());
            product.setImages(images);
        }
        return product;
    }
}