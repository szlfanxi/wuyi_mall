package com.wuyimall.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import com.wuyimall.service.AdminService;
import com.wuyimall.service.OrderService;
import com.wuyimall.repository.OrderRepository;
import com.wuyimall.entity.Order;
import com.wuyimall.dto.OrderDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AdminOrderController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    private Long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        return userId;
    }

    // 订单列表（所有用户的）
    @GetMapping("/list")
    public List<OrderDTO> list(HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);

        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
        List<OrderDTO> result = new ArrayList<>();
        for (Order order : orders) {
            // 假设orderService有根据订单ID获取订单详情的方法
            OrderDTO orderDTO = orderService.getOrderById(order.getId(), order.getUserId());
            result.add(orderDTO);
        }
        return result;
    }

    // 修改订单状态（比如发货/完成）
    @PostMapping("/changeStatus")
    public String changeStatus(@RequestParam Long orderId,
                               @RequestParam Integer status,
                               HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        order.setStatus(status);
        orderRepository.save(order);
        return "订单状态更新成功";
    }
    
    // 获取单个订单详情
    @GetMapping("/{orderId}")
    public OrderDTO getOrderDetail(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        return orderService.getOrderById(order.getId(), order.getUserId());
    }
}
