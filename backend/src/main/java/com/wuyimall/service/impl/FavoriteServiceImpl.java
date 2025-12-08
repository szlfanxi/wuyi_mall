package com.wuyimall.service.impl;

import com.wuyimall.entity.Favorite;
import com.wuyimall.entity.Product;
import com.wuyimall.repository.FavoriteRepository;
import com.wuyimall.repository.UserRepository;
import com.wuyimall.service.FavoriteService;
import com.wuyimall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductService productService;
    
    @Override
    public String addFavorite(Long userId, Long productId) {
        try {
            // 验证用户是否存在
            if (!userRepository.existsById(userId)) {
                return "用户不存在";
            }
            
            // 验证商品是否存在
            Product product = productService.getById(productId);
            if (product == null) {
                return "商品不存在";
            }
            
            // 检查是否已收藏
            if (isFavorite(userId, productId)) {
                return "已收藏";
            }
            
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);
            favoriteRepository.save(favorite);
            return "成功";
        } catch (DataIntegrityViolationException e) {
            // 处理唯一约束冲突
            return "已收藏";
        } catch (Exception e) {
            // 处理其他异常
            return "失败：" + e.getMessage();
        }
    }
    
    // 在FavoriteServiceImpl的removeFavorite方法中添加验证
    @Override
    public void removeFavorite(Long userId, Long productId) {
        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 验证商品是否存在
        Product product = productService.getById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        
        // 检查收藏是否存在
        if (!isFavorite(userId, productId)) {
            throw new IllegalArgumentException("收藏记录不存在");
        }
        
        // 执行删除操作
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }
    
    @Override
    public List<Favorite> getFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }
    
    @Override
    public boolean isFavorite(Long userId, Long productId) {
        return favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }
    
    @Override
    public long getFavoriteCount(Long userId) {
        return favoriteRepository.countByUserId(userId);
    }
}