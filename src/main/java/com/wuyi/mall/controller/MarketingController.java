package com.wuyi.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.Coupon;
import com.wuyi.mall.entity.DiscountActivity;
import com.wuyi.mall.service.MarketingService;
import com.wuyi.mall.util.ResultUtil;
import com.wuyi.mall.vo.CouponPublishVO;
import com.wuyi.mall.vo.CouponReceiveVO;
import com.wuyi.mall.vo.DiscountActivityCreateVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 营销管理控制器
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping
public class MarketingController {

    /**
     * 营销管理服务
     */
    @Autowired
    private MarketingService marketingService;

    /**
     * 发布优惠券（商家权限）
     *
     * @param authentication  认证信息
     * @param couponPublishVO 发布优惠券请求参数
     * @return 发布结果
     */
    @PostMapping("/coupons")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResultUtil.Result<Map<String, Long>> publishCoupon(Authentication authentication, @Valid @RequestBody CouponPublishVO couponPublishVO) {
        // 从认证信息中获取用户ID（商家ID）
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        // 这里假设商家ID就是shopId，实际项目中可能需要从用户关联的shop表中查询
        Long shopId = userId;

        // 调用服务发布优惠券
        Long couponId = marketingService.publishCoupon(shopId, couponPublishVO);

        // 构建返回结果
        Map<String, Long> result = new java.util.HashMap<>();
        result.put("couponId", couponId);

        return ResultUtil.success(result);
    }

    /**
     * 创建打折活动（商家权限）
     *
     * @param authentication             认证信息
     * @param discountActivityCreateVO   创建打折活动请求参数
     * @return 创建结果
     */
    @PostMapping("/discount-activities")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResultUtil.Result<Map<String, Long>> createDiscountActivity(Authentication authentication, @Valid @RequestBody DiscountActivityCreateVO discountActivityCreateVO) {
        // 从认证信息中获取用户ID（商家ID）
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        // 这里假设商家ID就是shopId，实际项目中可能需要从用户关联的shop表中查询
        Long shopId = userId;

        // 调用服务创建打折活动
        Long activityId = marketingService.createDiscountActivity(shopId, discountActivityCreateVO);

        // 构建返回结果
        Map<String, Long> result = new java.util.HashMap<>();
        result.put("activityId", activityId);

        return ResultUtil.success(result);
    }

    /**
     * 商家查询自家优惠券活动（商家权限）
     *
     * @param authentication 认证信息
     * @param couponType     优惠券类型（可选）
     * @param status         状态（生效/失效，可选）
     * @param pageNum        页码（默认1）
     * @param pageSize       每页条数（默认10）
     * @return 优惠券列表（分页）
     */
    @GetMapping("/merchant/coupons")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResultUtil.Result<Page<Coupon>> getMerchantCoupons(Authentication authentication, @RequestParam(required = false) String couponType, @RequestParam(required = false) Integer status, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        // 从认证信息中获取用户ID（商家ID）
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        // 这里假设商家ID就是shopId，实际项目中可能需要从用户关联的shop表中查询
        Long shopId = userId;

        // 调用服务查询优惠券列表
        Page<Coupon> coupons = marketingService.getMerchantCoupons(shopId, couponType, status, pageNum, pageSize);

        return ResultUtil.success(coupons);
    }

    /**
     * 商家查询自家打折活动（商家权限）
     *
     * @param authentication 认证信息
     * @param status         状态（生效/失效，可选）
     * @param pageNum        页码（默认1）
     * @param pageSize       每页条数（默认10）
     * @return 打折活动列表（分页）
     */
    @GetMapping("/merchant/discount-activities")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResultUtil.Result<Page<DiscountActivity>> getMerchantDiscountActivities(Authentication authentication, @RequestParam(required = false) Integer status, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        // 从认证信息中获取用户ID（商家ID）
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        // 这里假设商家ID就是shopId，实际项目中可能需要从用户关联的shop表中查询
        Long shopId = userId;

        // 调用服务查询打折活动列表
        Page<DiscountActivity> activities = marketingService.getMerchantDiscountActivities(shopId, status, pageNum, pageSize);

        return ResultUtil.success(activities);
    }

    /**
     * 管理员查询所有优惠券（管理员权限）
     *
     * @param shopId     商铺ID（可选）
     * @param couponType 优惠券类型（可选）
     * @param startTime  开始时间（可选）
     * @param endTime    结束时间（可选）
     * @param pageNum    页码（默认1）
     * @param pageSize   每页条数（默认10）
     * @return 优惠券列表（分页）
     */
    @GetMapping("/admin/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil.Result<Page<Coupon>> getAdminCoupons(@RequestParam(required = false) Long shopId, @RequestParam(required = false) String couponType, @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        // 调用服务查询优惠券列表
        Page<Coupon> coupons = marketingService.getAdminCoupons(shopId, couponType, startTime, endTime, pageNum, pageSize);

        return ResultUtil.success(coupons);
    }

    /**
     * 管理员查询所有打折活动（管理员权限）
     *
     * @param shopId     商铺ID（可选）
     * @param startTime  开始时间（可选）
     * @param endTime    结束时间（可选）
     * @param pageNum    页码（默认1）
     * @param pageSize   每页条数（默认10）
     * @return 打折活动列表（分页）
     */
    @GetMapping("/admin/discount-activities")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil.Result<Page<DiscountActivity>> getAdminDiscountActivities(@RequestParam(required = false) Long shopId, @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        // 调用服务查询打折活动列表
        Page<DiscountActivity> activities = marketingService.getAdminDiscountActivities(shopId, startTime, endTime, pageNum, pageSize);

        return ResultUtil.success(activities);
    }

    /**
     * 客户查询可领取优惠券（客户权限）
     *
     * @param shopId   商铺ID（可选）
     * @param pageNum  页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 可领取优惠券列表（分页）
     */
    @GetMapping("/available-coupons")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResultUtil.Result<Page<Coupon>> getAvailableCoupons(@RequestParam(required = false) Long shopId, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        // 调用服务查询可领取优惠券列表
        Page<Coupon> coupons = marketingService.getAvailableCoupons(shopId, pageNum, pageSize);

        return ResultUtil.success(coupons);
    }

    /**
     * 客户领取优惠券（客户权限）
     *
     * @param authentication  认证信息
     * @param couponReceiveVO 领取优惠券请求参数
     * @return 领取结果
     */
    @PostMapping("/coupons/receive")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResultUtil.Result<Map<String, Object>> receiveCoupon(Authentication authentication, @Valid @RequestBody CouponReceiveVO couponReceiveVO) {
        // 从认证信息中获取用户ID（客户ID）
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 调用服务领取优惠券
        Map<String, Object> result = marketingService.receiveCoupon(userId, couponReceiveVO.getCouponId());

        return ResultUtil.success(result);
    }

    /**
     * 客户查询已领优惠券（客户权限）
     *
     * @param authentication 认证信息
     * @param status         状态（可选：1-未使用，2-已使用，3-已过期）
     * @param pageNum        页码（默认1）
     * @param pageSize       每页条数（默认10）
     * @return 已领优惠券列表（分页）
     */
    @GetMapping("/coupons/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResultUtil.Result<Page<com.wuyi.mall.entity.UserCoupon>> getMyCoupons(Authentication authentication, @RequestParam(required = false) Integer status, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        // 从认证信息中获取用户ID（客户ID）
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 调用服务查询已领优惠券列表
        Page<com.wuyi.mall.entity.UserCoupon> coupons = marketingService.getMyCoupons(userId, status, pageNum, pageSize);

        return ResultUtil.success(coupons);
    }

}