package com.wuyimall.service;

import com.wuyimall.dto.CartItemDTO;
import java.util.List;

public interface CartService {

    void addToCart(Long userId, Long productId, Integer quantity);

    List<CartItemDTO> listCart(Long userId);

    void updateQuantity(Long userId, Long productId, Integer quantity);

    void removeItem(Long userId, Long productId);
}