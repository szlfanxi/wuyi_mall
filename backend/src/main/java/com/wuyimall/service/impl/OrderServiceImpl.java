package com.wuyimall.service.impl;

import com.wuyimall.dto.CreateOrderRequest;
import com.wuyimall.dto.OrderDTO;
import com.wuyimall.dto.OrderItemDTO;
import com.wuyimall.entity.Order;
import com.wuyimall.entity.OrderItem;
import com.wuyimall.entity.Product;
import com.wuyimall.repository.OrderRepository;
import com.wuyimall.repository.OrderItemRepository;
import com.wuyimall.repository.ProductRepository;
import com.wuyimall.repository.UserRepository;
import com.wuyimall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现类
 * 实现订单相关的业务逻辑
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNo() {
        // 生成订单号：时间戳 + UUID前8位
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + uuid;
    }
    
    /**
     * Order转OrderDTO
     * @param order 订单实体
     * @return 订单DTO
     */
    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderNo(order.getOrderNo());
        orderDTO.setUserId(order.getUserId());
        
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCreateTime(order.getCreateTime());
        
        // 查询订单商品项
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
        
        // 初始化收货人姓名
        String receiveName = "未知用户";
        
        for (OrderItem orderItem : orderItems) {
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setProductId(orderItem.getProductId());
            orderItemDTO.setProductName(orderItem.getProductName());
            orderItemDTO.setProductPrice(orderItem.getProductPrice());
            orderItemDTO.setQuantity(orderItem.getQuantity());
            orderItemDTO.setTotalPrice(orderItem.getTotalPrice());
            
            // 设置收货人信息
            orderItemDTO.setReceiveName(orderItem.getReceiveName());
            orderItemDTO.setReceivePhone(orderItem.getReceivePhone());
            orderItemDTO.setReceiveAddress(orderItem.getReceiveAddress());
            
            // 查询商品图片
            Product product = productRepository.findById(orderItem.getProductId()).orElse(null);
            if (product != null) {
                orderItemDTO.setProductImage(product.getMainImage());
            }
            
            orderItemDTOs.add(orderItemDTO);
            
            // 获取收货人姓名（使用第一个商品项的收货人信息）
            if (orderItem.getReceiveName() != null) {
                receiveName = orderItem.getReceiveName();
            }
        }
        
        // 设置订单用户名（收货人姓名）
        orderDTO.setUsername(receiveName);
        
        orderDTO.setOrderItems(orderItemDTOs);
        
        return orderDTO;
    }
    
    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request, Long userId) {
        // 1. 生成订单号
        String orderNo = generateOrderNo();
        
        // 2. 创建订单实体
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(0); // 初始状态：未支付
        
        // 3. 保存订单
        order = orderRepository.save(order);
        
        // 4. 保存订单商品项
        for (OrderItemDTO itemDTO : request.getOrderItems()) {
            // 验证商品库存
            Product product = productRepository.findById(itemDTO.getProductId()).orElse(null);
            if (product == null) {
                throw new RuntimeException("商品不存在：" + itemDTO.getProductName());
            }
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new RuntimeException("商品库存不足：" + product.getName());
            }
            
            // 创建订单商品项
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(itemDTO.getProductId());
            orderItem.setProductName(itemDTO.getProductName());
            orderItem.setProductPrice(itemDTO.getProductPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setTotalPrice(itemDTO.getTotalPrice());
            
            // 保存收货人信息
            orderItem.setReceiveName(request.getName());
            orderItem.setReceivePhone(request.getPhone());
            orderItem.setReceiveAddress(request.getAddress());
            
            // 保存订单商品项
            orderItemRepository.save(orderItem);
            
            // 扣减商品库存
            product.setStock(product.getStock() - itemDTO.getQuantity());
            productRepository.save(product);
        }
        
        // 5. 转换为DTO并返回
        return convertToOrderDTO(order);
    }
    
    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderDTO> orderDTOs = new ArrayList<>();
        
        for (Order order : orders) {
            orderDTOs.add(convertToOrderDTO(order));
        }
        
        return orderDTOs;
    }
    
    @Override
    public OrderDTO getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUserId().equals(userId)) {
            return null;
        }
        return convertToOrderDTO(order);
    }
    
    @Override
    public OrderDTO getOrderByOrderNo(String orderNo, Long userId) {
        Order order = orderRepository.findByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            return null;
        }
        return convertToOrderDTO(order);
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有未支付的订单可以取消
        if (order.getStatus() != 0) {
            return false;
        }
        
        // 更新订单状态为已取消
        order.setStatus(4); // 4：已取消
        orderRepository.save(order);
        
        // 恢复商品库存
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            Product product = productRepository.findById(orderItem.getProductId()).orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + orderItem.getQuantity());
                productRepository.save(product);
            }
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean payOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有未支付的订单可以支付
        if (order.getStatus() != 0) {
            return false;
        }
        
        // 更新订单状态为已支付
        order.setStatus(1); // 1：已支付
        orderRepository.save(order);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean confirmReceipt(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有已发货的订单可以确认收货
        if (order.getStatus() != 2) {
            return false;
        }
        
        // 更新订单状态为已完成
        order.setStatus(3); // 3：已完成
        orderRepository.save(order);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean deleteOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        // 只有已取消或已完成的订单可以删除
        if (order.getStatus() != 3 && order.getStatus() != 4) {
            return false;
        }
        
        // 删除订单商品项
        orderItemRepository.deleteByOrderId(orderId);
        
        // 删除订单
        orderRepository.delete(order);
        
        return true;
    }
}
