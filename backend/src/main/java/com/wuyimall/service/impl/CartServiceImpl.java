package com.wuyimall.service.impl;

import com.wuyimall.dto.CartItemDTO;
import com.wuyimall.entity.CartItem;
import com.wuyimall.entity.Product;
import com.wuyimall.repository.CartItemRepository;
import com.wuyimall.repository.ProductRepository;
import com.wuyimall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public void addToCart(Long userId, Long productId, Integer quantity) {
        // 1. 检查商品是否存在
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        System.err.println("productId: " + productId);
        // 2. 检查库存是否充足
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }
        
        // 3. 处理购物车添加逻辑
        CartItem existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        
        if (existingItem != null) {
            // 检查添加后总数量是否超过库存
            int totalQuantity = existingItem.getQuantity() + quantity;
            if (totalQuantity > product.getStock()) {
                throw new RuntimeException("库存不足");
            }
            existingItem.setQuantity(totalQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }

    @Override
    public List<CartItemDTO> listCart(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId()).orElse(null);
            if (product != null) {
                CartItemDTO cartItemDTO = new CartItemDTO();
                cartItemDTO.setProductId(product.getId());
                cartItemDTO.setProductName(product.getName());
                cartItemDTO.setProductImage(product.getMainImage());
                cartItemDTO.setProductPrice(product.getPrice());
                cartItemDTO.setQuantity(cartItem.getQuantity());
                cartItemDTO.setTotalPrice(product.getPrice().multiply(new java.math.BigDecimal(cartItem.getQuantity())));
                cartItemDTOs.add(cartItemDTO);
            }
        }
        
        return cartItemDTOs;
    }

    @Override
    @Transactional
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    @Transactional
    public void removeItem(Long userId, Long productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }
}