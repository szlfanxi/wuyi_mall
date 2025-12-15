package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 优惠券Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * 按商铺ID和状态查询优惠券（分页）
     *
     * @param page     分页参数
     * @param shopId   商铺ID
     * @param status   状态（可选）
     * @param couponType 优惠券类型（可选）
     * @return 优惠券列表
     */
    Page<Coupon> selectByShopId(Page<Coupon> page, @Param("shopId") Long shopId, @Param("status") Integer status, @Param("couponType") String couponType);

    /**
     * 按状态和时间查询有效优惠券（用于缓存）
     *
     * @param shopId 商铺ID（可选）
     * @return 优惠券列表
     */
    java.util.List<Coupon> selectValidCoupons(@Param("shopId") Long shopId);

    /**
     * 管理员查询所有优惠券（分页，支持多条件筛选）
     *
     * @param page       分页参数
     * @param params     查询参数，包含shopId、couponType、时间范围等
     * @return 优惠券列表
     */
    Page<Coupon> selectByAdmin(Page<Coupon> page, @Param("params") Map<String, Object> params);

    /**
     * 扣减优惠券剩余库存
     *
     * @param couponId 优惠券ID
     * @return 更新数量
     */
    int decreaseRemainNum(@Param("id") Long couponId);

    /**
     * 根据ID查询优惠券（包含商品信息）
     *
     * @param couponId 优惠券ID
     * @return 优惠券信息
     */
    Coupon selectWithProductInfo(@Param("id") Long couponId);

}