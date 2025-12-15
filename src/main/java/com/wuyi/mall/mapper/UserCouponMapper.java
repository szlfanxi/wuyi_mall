package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 客户优惠券关联Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    /**
     * 按用户ID和状态查询优惠券（分页）
     *
     * @param page     分页参数
     * @param userId   用户ID
     * @param status   状态（可选）
     * @return 用户优惠券列表
     */
    Page<UserCoupon> selectByUserId(Page<UserCoupon> page, @Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 检查用户是否已领取该优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 1表示已领取，0表示未领取
     */
    int checkUserCouponExists(@Param("userId") Long userId, @Param("couponId") Long couponId);

    /**
     * 使用优惠券（更新状态和使用时间）
     *
     * @param userCouponId 用户优惠券ID
     * @param orderId      订单ID
     * @return 更新数量
     */
    int useCoupon(@Param("id") Long userCouponId, @Param("orderId") Long orderId);

    /**
     * 查询用户可用的优惠券列表（用于下单时选择）
     *
     * @param userId  用户ID
     * @param shopId  商铺ID
     * @param totalAmount 订单总金额
     * @return 可用优惠券列表
     */
    java.util.List<UserCoupon> selectAvailableCoupons(@Param("userId") Long userId, @Param("shopId") Long shopId, @Param("totalAmount") java.math.BigDecimal totalAmount);

    /**
     * 按订单ID查询使用的优惠券
     *
     * @param orderId 订单ID
     * @return 用户优惠券信息
     */
    UserCoupon selectByOrderId(@Param("orderId") Long orderId);

}