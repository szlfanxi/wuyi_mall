package com.wuyimall.controller;

import com.wuyimall.repository.ProductRepository;
import com.wuyimall.repository.UserRepository;
import com.wuyimall.repository.OrderRepository;
import com.wuyimall.entity.Order;
import com.wuyimall.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AdminDashboardController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    // 获取统计数据
    @GetMapping("/stats")
    public Map<String, Object> getStats(HttpServletRequest request) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取商品总数：只统计状态为1（上架）的商品
        long productCount = productRepository.countByStatus(1);
        stats.put("productCount", productCount);
        
        // 获取用户总数：直接使用userRepository.count()获取user表的记录总数
        stats.put("userCount", userRepository.count());
        
        // 获取订单总数：直接使用orderRepository.count()获取order表的记录总数
        stats.put("orderCount", orderRepository.count());
        
        // 获取总销售额：查询所有订单并计算总金额
        List<Order> allOrders = orderRepository.findAll();
        double totalSales = allOrders.stream()
                .map(order -> order.getTotalAmount().doubleValue())
                .mapToDouble(Double::doubleValue)
                .sum();
        stats.put("totalSales", totalSales);
        
        return stats;
    }
    
    // 获取最近订单
    @GetMapping("/recent-orders")
    public List<Map<String, Object>> getRecentOrders(HttpServletRequest request) {
        // 获取最近5条订单
        List<Order> orders = orderRepository.findTop5ByOrderByIdDesc();
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Order order : orders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("orderNo", order.getOrderNo());
            
            // 根据userId获取用户名
            User user = userRepository.findById(order.getUserId()).orElse(null);
            String username = user != null ? user.getUsername() : "未知用户";
            orderMap.put("username", username);
            
            orderMap.put("totalAmount", order.getTotalAmount());
            orderMap.put("status", order.getStatus());
            orderMap.put("statusText", getStatusText(order.getStatus()));
            orderMap.put("createTime", order.getCreateTime());
            result.add(orderMap);
        }
        
        return result;
    }
    
    // 将订单状态转换为文本
    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待付款";
            case 1: return "已付款";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知状态";
        }
    }
}