package com.wuyimall.repository;

import com.wuyimall.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategoryId(Long categoryId);

    /**
     * 根据状态统计商品数量
     * @param status 商品状态
     * @return 商品数量
     */
    long countByStatus(Integer status);

}