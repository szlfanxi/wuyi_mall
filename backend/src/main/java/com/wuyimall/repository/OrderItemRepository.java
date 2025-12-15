package com.wuyimall.repository;

import com.wuyimall.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 订单商品项Repository接口
 * 继承JpaRepository，提供基本的CRUD操作
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * 根据订单ID查询订单商品项列表
     * @param orderId 订单ID
     * @return 订单商品项列表
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * 根据商品ID查询订单商品项列表
     * @param productId 商品ID
     * @return 订单商品项列表
     */
    List<OrderItem> findByProductId(Long productId);
    
    /**
     * 根据订单ID和商品ID查询订单商品项
     * @param orderId 订单ID
     * @param productId 商品ID
     * @return 订单商品项对象
     */
    OrderItem findByOrderIdAndProductId(Long orderId, Long productId);
    
    /**
     * 根据订单ID删除订单商品项
     * @param orderId 订单ID
     */
    void deleteByOrderId(Long orderId);
}
