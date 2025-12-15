package com.wuyi.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.Coupon;
import com.wuyi.mall.entity.DiscountActivity;
import com.wuyi.mall.vo.CouponPublishVO;
import com.wuyi.mall.vo.DiscountActivityCreateVO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 营销管理服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface MarketingService {

    /**
     * 发布优惠券
     *
     * @param shopId           商铺ID
     * @param couponPublishVO  发布优惠券请求参数
     * @return 优惠券ID
     */
    Long publishCoupon(Long shopId, CouponPublishVO couponPublishVO);

    /**
     * 创建打折活动
     *
     * @param shopId                    商铺ID
     * @param discountActivityCreateVO  创建打折活动请求参数
     * @return 活动ID
     */
    Long createDiscountActivity(Long shopId, DiscountActivityCreateVO discountActivityCreateVO);

    /**
     * 商家查询自家活动（优惠券）
     *
     * @param shopId     商铺ID
     * @param couponType 优惠券类型（可选）
     * @param status     状态（生效/失效，可选）
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 优惠券列表（分页）
     */
    Page<Coupon> getMerchantCoupons(Long shopId, String couponType, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 商家查询自家活动（打折活动）
     *
     * @param shopId  商铺ID
     * @param status  状态（生效/失效，可选）
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 打折活动列表（分页）
     */
    Page<DiscountActivity> getMerchantDiscountActivities(Long shopId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 管理员查询所有优惠券
     *
     * @param shopId     商铺ID（可选）
     * @param couponType 优惠券类型（可选）
     * @param startTime  开始时间（可选）
     * @param endTime    结束时间（可选）
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 优惠券列表（分页）
     */
    Page<Coupon> getAdminCoupons(Long shopId, String couponType, String startTime, String endTime, Integer pageNum, Integer pageSize);

    /**
     * 管理员查询所有打折活动
     *
     * @param shopId     商铺ID（可选）
     * @param startTime  开始时间（可选）
     * @param endTime    结束时间（可选）
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 打折活动列表（分页）
     */
    Page<DiscountActivity> getAdminDiscountActivities(Long shopId, String startTime, String endTime, Integer pageNum, Integer pageSize);

    /**
     * 客户查询可领取优惠券
     *
     * @param shopId   商铺ID（可选）
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 可领取优惠券列表（分页）
     */
    Page<Coupon> getAvailableCoupons(Long shopId, Integer pageNum, Integer pageSize);

    /**
     * 客户领取优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 领取结果
     */
    Map<String, Object> receiveCoupon(Long userId, Long couponId);

    /**
     * 查询客户已领优惠券
     *
     * @param userId   用户ID
     * @param status   状态（可选：1-未使用，2-已使用，3-已过期）
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 客户优惠券列表（分页）
     */
    Page<com.wuyi.mall.entity.UserCoupon> getMyCoupons(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 检查活动有效性
     *
     * @param activityId 活动ID
     * @param activityType 活动类型（COUPON/DISCOUNT）
     * @return 活动是否有效
     */
    boolean checkActivityValid(Long activityId, String activityType);

    /**
     * 检查优惠券可用性
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @param shopId   商铺ID
     * @param totalAmount 订单总金额
     * @return 优惠券是否可用
     */
    boolean checkCouponUsable(Long userId, Long couponId, Long shopId, BigDecimal totalAmount);

    /**
     * 计算优惠金额
     *
     * @param totalAmount 订单总金额
     * @param couponId    优惠券ID（可选）
     * @param productId   商品ID（可选，用于打折活动）
     * @return 优惠金额
     */
    BigDecimal calculateDiscountAmount(BigDecimal totalAmount, Long couponId, Long productId);

    /**
     * 使用优惠券（下单时）
     *
     * @param userCouponId 用户优惠券ID
     * @param orderId      订单ID
     * @return 使用结果
     */
    boolean useCoupon(Long userCouponId, Long orderId);

}