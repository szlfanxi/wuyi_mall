package com.wuyimall.controller;

import com.wuyimall.dto.OrderDTO;
import com.wuyimall.dto.CreateOrderRequest;
import com.wuyimall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 * 处理订单相关的HTTP请求
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 从请求中获取用户ID
     * @param request HTTP请求对象
     * @return 用户ID
     */
    private Long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        return userId;
    }

    /**
     * 获取订单列表
     * @param request HTTP请求对象
     * @return 订单列表
     */
    @GetMapping
    public List<OrderDTO> list(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
        
        // 设置订单状态文本
        for (OrderDTO order : orders) {
            switch (order.getStatus()) {
                case 0:
                    order.setStatusText("客户下单");
                    break;
                case 1:
                    order.setStatusText("商家确认");
                    break;
                case 2:
                    order.setStatusText("备货完成");
                    break;
                case 3:
                    order.setStatusText("开始发货");
                    break;
                case 4:
                    order.setStatusText("交易完成");
                    break;
                case 5:
                    order.setStatusText("已取消");
                    break;
                default:
                    order.setStatusText("未知状态");
            }
        }
        
        return orders;
    }

    /**
     * 取消订单
     * @param orderId 订单ID
     * @param request HTTP请求对象
     * @return 取消结果
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(request);
            boolean result = orderService.cancelOrder(orderId, userId);
            if (result) {
                response.put("success", true);
                response.put("message", "订单取消成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "订单取消失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 支付订单
     * @param orderId 订单ID
     * @param request HTTP请求对象
     * @return 支付结果
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Map<String, Object>> payOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(request);
            boolean result = orderService.payOrder(orderId, userId);
            if (result) {
                response.put("success", true);
                response.put("message", "支付成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "支付失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 确认收货
     * @param orderId 订单ID
     * @param request HTTP请求对象
     * @return 确认结果
     */
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Map<String, Object>> confirmReceipt(@PathVariable Long orderId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(request);
            boolean result = orderService.confirmReceipt(orderId, userId);
            if (result) {
                response.put("success", true);
                response.put("message", "确认收货成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "确认收货失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除订单
     * @param orderId 订单ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    @PostMapping("/{orderId}/delete")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(request);
            boolean result = orderService.deleteOrder(orderId, userId);
            if (result) {
                response.put("success", true);
                response.put("message", "订单删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "订单删除失败");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 创建订单
     * @param requestDTO 创建订单请求DTO
     * @param request HTTP请求对象
     * @return 创建结果
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody CreateOrderRequest requestDTO, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(request);
            OrderDTO order = orderService.createOrder(requestDTO, userId);
            response.put("success", true);
            response.put("message", "订单创建成功");
            response.put("order", order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
