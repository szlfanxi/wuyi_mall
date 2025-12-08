package com.wuyimall.service;

import com.wuyimall.entity.Favorite;
import java.util.List;

public interface FavoriteService {
    
    /**
     * 添加商品到收藏夹
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 操作结果状态（成功、已收藏、失败及原因）
     */
    String addFavorite(Long userId, Long productId);
    
    /**
     * 从收藏夹移除商品
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void removeFavorite(Long userId, Long productId);
    
    /**
     * 获取用户的收藏列表
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<Favorite> getFavorites(Long userId);
    
    /**
     * 检查商品是否已被收藏
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long userId, Long productId);
    
    /**
     * 获取用户收藏数量
     * @param userId 用户ID
     * @return 收藏数量
     */
    long getFavoriteCount(Long userId);
}