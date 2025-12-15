package com.wuyimall.dto;

import java.math.BigDecimal;

/**
 * 订单商品项DTO
 * 用于前端显示订单商品项信息
 */
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private String productImage; // 商品图片
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalPrice; // 商品小计
    private String receiveName; // 收货人姓名
    private String receivePhone; // 收货人手机
    private String receiveAddress; // 收货人地址

    // getter 方法
    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public String getReceivePhone() {
        return receivePhone;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    // setter 方法
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public void setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }
}
