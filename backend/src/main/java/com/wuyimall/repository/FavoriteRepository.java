package com.wuyimall.repository;

import com.wuyimall.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * 根据用户ID查找所有收藏商品
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<Favorite> findByUserId(Long userId);
    
    /**
     * 查找用户是否收藏了特定商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 收藏记录（如果存在）
     */
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 删除用户的特定收藏商品
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 统计用户的收藏数量
     * @param userId 用户ID
     * @return 收藏数量
     */
    long countByUserId(Long userId);
}