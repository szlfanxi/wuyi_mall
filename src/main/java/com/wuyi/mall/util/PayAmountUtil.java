package com.wuyi.mall.util;

import com.wuyi.mall.entity.Order;
import com.wuyi.mall.entity.OrderItem;
import com.wuyi.mall.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付金额计算工具类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Component
public class PayAmountUtil {

    private static MarketingService marketingService;

    @Autowired
    public void setMarketingService(MarketingService marketingService) {
        PayAmountUtil.marketingService = marketingService;
    }

    /**
     * 计算订单实付金额
     *
     * @param order      订单信息
     * @param orderItems 订单商品列表
     * @param couponId   优惠券ID（可选）
     * @return 实付金额
     */
    public static BigDecimal calculateActualPayAmount(Order order, List<OrderItem> orderItems, Long couponId) {
        // 订单总金额
        BigDecimal totalAmount = order.getTotalAmount();
        // 计算优惠券优惠金额
        BigDecimal couponDiscount = calculateCouponDiscount(totalAmount, couponId);
        // 计算商品打折优惠金额
        BigDecimal discountAmount = calculateProductDiscount(orderItems);
        // 实付金额 = 订单总金额 - 优惠券优惠 - 商品打折优惠
        BigDecimal actualPayAmount = totalAmount.subtract(couponDiscount).subtract(discountAmount);
        // 确保实付金额不小于0
        if (actualPayAmount.compareTo(BigDecimal.ZERO) < 0) {
            actualPayAmount = BigDecimal.ZERO;
        }
        return actualPayAmount;
    }

    /**
     * 计算优惠券优惠金额
     *
     * @param totalAmount 订单总金额
     * @param couponId    优惠券ID
     * @return 优惠券优惠金额
     */
    private static BigDecimal calculateCouponDiscount(BigDecimal totalAmount, Long couponId) {
        if (couponId == null) {
            return BigDecimal.ZERO;
        }
        // 调用营销服务计算优惠券优惠金额
        return marketingService.calculateDiscountAmount(totalAmount, couponId, null);
    }

    /**
     * 计算商品打折优惠金额
     *
     * @param orderItems 订单商品列表
     * @return 商品打折优惠总金额
     */
    private static BigDecimal calculateProductDiscount(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            // 计算单个商品的原价总额
            BigDecimal productTotal = orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()));
            // 调用营销服务计算商品打折优惠金额
            BigDecimal productDiscount = marketingService.calculateDiscountAmount(productTotal, null, orderItem.getProductId());
            totalDiscount = totalDiscount.add(productDiscount);
        }
        return totalDiscount;
    }

    /**
     * 格式化支付金额（保留两位小数）
     *
     * @param amount 金额
     * @return 格式化后的金额
     */
    public static BigDecimal formatAmount(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 检查订单是否超时
     *
     * @param order 订单信息
     * @param timeoutMinutes 超时分钟数
     * @return 是否超时
     */
    public static boolean checkOrderTimeout(Order order, int timeoutMinutes) {
        if (order == null || order.getCreateTime() == null) {
            return true;
        }
        // 计算超时时间
        java.time.LocalDateTime timeoutTime = order.getCreateTime().plusMinutes(timeoutMinutes);
        // 当前时间
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        // 判断订单是否超时
        return now.isAfter(timeoutTime);
    }
}
