package com.wuyimall.service.impl;

import com.wuyimall.entity.Favorite;
import com.wuyimall.entity.Product;
import com.wuyimall.repository.FavoriteRepository;
import com.wuyimall.repository.ProductRepository;
import com.wuyimall.repository.UserRepository;
import com.wuyimall.service.FavoriteService;
import com.wuyimall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductService productService;
    
    @Override
    @Transactional
    public String addFavorite(Long userId, Long productId) {
        try {
            // 验证用户是否存在
            if (!userRepository.existsById(userId)) {
                return "用户不存在";
            }
            
            // 验证商品是否存在，使用existsById方法，避免加载关联图片
            if (!productRepository.existsById(productId)) {
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
    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        // 模仿购物车删除逻辑，直接调用repository删除
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