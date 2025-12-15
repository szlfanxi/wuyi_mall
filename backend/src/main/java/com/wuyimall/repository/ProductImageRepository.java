package com.wuyimall.repository;

import com.wuyimall.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    /**
     * 根据商品ID查询图片列表
     * @param productId 商品ID
     * @return 商品图片列表
     */
    List<ProductImage> findByProductId(Long productId);
}
