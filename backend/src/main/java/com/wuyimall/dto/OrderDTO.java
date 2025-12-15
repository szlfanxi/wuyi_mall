package com.wuyimall.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单DTO
 * 用于前端显示订单信息
 */
public class OrderDTO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String username; // 用户名（收货人名称）
    private BigDecimal totalAmount;
    private Integer status;
    private String statusText; // 订单状态文本（0未支付、1已支付、2已发货、3已完成）
    private LocalDateTime createTime;
    private List<OrderItemDTO> orderItems; // 订单商品项列表

    // getter 方法
    public Long getId() {
        return id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusText() {
        return statusText;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    // setter 方法
    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(Integer status) {
        this.status = status;
        // 根据状态值自动设置状态文本
        switch (status) {
            case 0:
                this.statusText = "客户下单";
                break;
            case 1:
                this.statusText = "商家确认";
                break;
            case 2:
                this.statusText = "备货完成";
                break;
            case 3:
                this.statusText = "开始发货";
                break;
            case 4:
                this.statusText = "交易完成";
                break;
            case 5:
                this.statusText = "已取消";
                break;
            default:
                this.statusText = "未知状态";
        }
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}
