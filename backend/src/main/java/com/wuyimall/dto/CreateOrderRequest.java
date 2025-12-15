package com.wuyimall.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单请求DTO
 * 用于前端提交订单创建请求时传递的订单数据
 */
public class CreateOrderRequest {
    private List<OrderItemDTO> orderItems; // 订单商品项列表
    private String addressId; // 收货地址ID
    private String paymentMethod; // 支付方式，如微信支付、支付宝支付等
    private BigDecimal totalAmount; // 订单总金额
    private String name; // 联系人姓名
    private String phone; // 联系电话
    private String address; // 收货地址
    private String remark; // 订单备注

    // getter 方法
    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public String getAddressId() {
        return addressId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getRemark() {
        return remark;
    }

    // setter 方法
    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}