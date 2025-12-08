package com.wuyimall.controller;

import com.wuyimall.entity.Favorite;
import com.wuyimall.service.FavoriteService;
import com.wuyimall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    /**
     * 获取用户收藏列表
     * @param request HTTP请求，用于获取当前用户ID
     * @return 收藏列表
     */
    @GetMapping("/favorites")
    public ResponseEntity<Map<String, Object>> getFavorites(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                throw new RuntimeException("用户未登录");
            }
            
            List<Favorite> favorites = favoriteService.getFavorites(userId);
            response.put("success", true);
            response.put("favorites", favorites);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 添加商品到收藏
     * @param productId 商品ID
     * @param request HTTP请求，用于获取当前用户ID
     * @return 操作结果
     */
    @PostMapping("/favorites/add")
    public ResponseEntity<Map<String, Object>> addFavorite(
            @RequestParam Long productId,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.badRequest().body(response);
            }
            
            String result = favoriteService.addFavorite(userId, productId);
            if ("成功".equals(result)) {
                response.put("success", true);
                response.put("message", "商品已添加到收藏");
                return ResponseEntity.ok(response);
            } else if ("已收藏".equals(result)) {
                response.put("success", true);
                response.put("message", "商品已在收藏列表中");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", result);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "操作失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 从收藏中移除商品
     * @param productId 请求体中的商品ID
     * @param request HTTP请求，用于获取当前用户ID
     * @return 操作结果
     */
    @PostMapping("/favorites/remove")
    public ResponseEntity<Map<String, Object>> removeFavorite(
            @RequestParam Long productId,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.badRequest().body(response);
            }
            
            favoriteService.removeFavorite(userId, productId);
            response.put("success", true);
            response.put("message", "商品已从收藏中移除");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "操作失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 检查商品是否已收藏
     * @param productId 商品ID
     * @param request HTTP请求，用于获取当前用户ID
     * @return 检查结果
     */
    @GetMapping("/favorites/check")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @RequestParam Long productId,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                response.put("success", true);
                response.put("isFavorite", false);
                return ResponseEntity.ok(response);
            }
            
            boolean isFavorite = favoriteService.isFavorite(userId, productId);
            response.put("success", true);
            response.put("isFavorite", isFavorite);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取用户收藏数量
     * @param request HTTP请求，用于获取当前用户ID
     * @return 收藏数量
     */
    @GetMapping("/favorites/count")
    public ResponseEntity<Map<String, Object>> getFavoriteCount(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                response.put("success", true);
                response.put("count", 0);
                return ResponseEntity.ok(response);
            }
            
            long count = favoriteService.getFavoriteCount(userId);
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}