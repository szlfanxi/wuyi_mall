package com.wuyimall.service;

import com.wuyimall.dto.CreateOrderRequest;
import com.wuyimall.dto.OrderDTO;
import com.wuyimall.entity.Order;
import java.util.List;

/**
 * 订单服务接口
 * 定义订单相关的业务逻辑方法
 */
public interface OrderService {
    
    /**
     * 创建订单
     * @param request 创建订单请求DTO
     * @param userId 用户ID
     * @return 订单DTO
     */
    OrderDTO createOrder(CreateOrderRequest request, Long userId);
    
    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 订单DTO列表
     */
    List<OrderDTO> getOrdersByUserId(Long userId);
    
    /**
     * 根据订单ID查询订单详情
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 订单DTO
     */
    OrderDTO getOrderById(Long orderId, Long userId);
    
    /**
     * 根据订单号查询订单详情
     * @param orderNo 订单号
     * @param userId 用户ID
     * @return 订单DTO
     */
    OrderDTO getOrderByOrderNo(String orderNo, Long userId);
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 取消结果
     */
    boolean cancelOrder(Long orderId, Long userId);
    
    /**
     * 支付订单
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 支付结果
     */
    boolean payOrder(Long orderId, Long userId);
    
    /**
     * 确认收货
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 确认结果
     */
    boolean confirmReceipt(Long orderId, Long userId);
    
    /**
     * 删除订单
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean deleteOrder(Long orderId, Long userId);
}
