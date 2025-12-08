package com.wuyimall.service;

import com.wuyimall.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> listByCategory(Long categoryId);
    Product getById(Long id);
}