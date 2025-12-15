package com.wuyimall.controller;

import com.wuyimall.entity.CartItem;
import com.wuyimall.entity.Product;
import com.wuyimall.dto.CartItemDTO;
import com.wuyimall.service.CartService;
import com.wuyimall.repository.CartItemRepository;
import com.wuyimall.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class CartController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;

    private Long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        return userId;
    }

    // 1. 加入购物车
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("Request Body: " + requestBody);
            
            // 直接从请求体中获取参数
            Long productId = Long.parseLong(requestBody.get("productId").toString());
            Integer quantity = Integer.parseInt(requestBody.get("quantity").toString());
            
            // 从请求头中获取token
            String authHeader = request.getHeader("Authorization");
            System.out.println("Authorization Header: " + authHeader);
            
            // 获取所有请求属性，调试用
            System.out.println("Request Attributes:");
            request.getAttributeNames().asIterator().forEachRemaining(name -> {
                System.out.println(name + ": " + request.getAttribute(name));
            });
            
            // 验证token并获取userId
            Long userId = getUserId(request);
            System.out.println("UserId: " + userId);
            System.out.println("ProductId: " + productId);
            System.out.println("Quantity: " + quantity);
            
            // 直接在Controller中添加购物车记录，不调用服务层
            // 检查商品是否存在
            System.out.println("Calling productRepository.findById(" + productId + ")");
            Product product = productRepository.findById(productId).orElse(null);
            System.out.println("Found product: " + product);
            if (product == null) {
                // 查询所有商品，看看有哪些商品
                List<Product> allProducts = productRepository.findAll();
                System.out.println("All products: " + allProducts);
                throw new RuntimeException("商品不存在，当前商品ID: " + productId);
            }
            
            // 检查库存是否充足
            if (product.getStock() < quantity) {
                throw new RuntimeException("库存不足");
            }
            
            // 处理购物车添加逻辑
            CartItem existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
            
            if (existingItem != null) {
                // 检查添加后总数量是否超过库存
                int totalQuantity = existingItem.getQuantity() + quantity;
                if (totalQuantity > product.getStock()) {
                    throw new RuntimeException("库存不足");
                }
                existingItem.setQuantity(totalQuantity);
                cartItemRepository.save(existingItem);
                System.out.println("Updated existing cart item: " + existingItem.getId());
            } else {
                CartItem newItem = new CartItem();
                newItem.setUserId(userId);
                newItem.setProductId(productId);
                newItem.setQuantity(quantity);
                cartItemRepository.save(newItem);
                System.out.println("Created new cart item: " + newItem.getId());
            }
            
            response.put("success", true);
            response.put("message", "加入购物车成功");
            return response;
        } catch (Exception e) {
            System.err.println("加入购物车失败: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("errorDetails", e.toString());
            return response;
        }
    }

    // 2. 查看购物车列表
    @GetMapping("/list")
    public List<CartItemDTO> list(HttpServletRequest request) {
        Long userId = getUserId(request);
        return cartService.listCart(userId);
    }

    // 3. 修改数量
    @PostMapping("/update")
    public String update(@RequestParam Long productId,
                         @RequestParam Integer quantity,
                         HttpServletRequest request) {
        Long userId = getUserId(request);
        cartService.updateQuantity(userId, productId, quantity);
        return "修改成功";
    }

    // 4. 删除
    @PostMapping("/remove")
    public String remove(@RequestParam Long productId,
                         HttpServletRequest request) {
        Long userId = getUserId(request);
        cartService.removeItem(userId, productId);
        return "删除成功";
    }
}