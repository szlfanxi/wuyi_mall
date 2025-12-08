package com.wuyimall.repository;

import com.wuyimall.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 订单Repository接口
 * 继承JpaRepository，提供基本的CRUD操作
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据订单号查询订单
     * @param orderNo 订单号
     * @return 订单对象
     */
    Order findByOrderNo(String orderNo);
    
    /**
     * 根据用户ID和订单状态查询订单列表
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByUserIdAndStatus(Long userId, Integer status);
    
    /**
     * 获取最近5条订单，按ID倒序排列
     * @return 最近5条订单列表
     */
    List<Order> findTop5ByOrderByIdDesc();
}
