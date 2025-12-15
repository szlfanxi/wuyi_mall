package com.wuyi.mall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.*;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.*;
import com.wuyi.mall.service.MarketingService;
import com.wuyi.mall.vo.CouponPublishVO;
import com.wuyi.mall.vo.DiscountActivityCreateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 营销管理服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class MarketingServiceImpl implements MarketingService {

    /**
     * 优惠券Mapper
     */
    @Autowired
    private CouponMapper couponMapper;

    /**
     * 打折活动Mapper
     */
    @Autowired
    private DiscountActivityMapper discountActivityMapper;

    /**
     * 活动商品关联Mapper
     */
    @Autowired
    private DiscountActivityProductMapper discountActivityProductMapper;

    /**
     * 客户优惠券Mapper
     */
    @Autowired
    private UserCouponMapper userCouponMapper;

    /**
     * 商品Mapper
     */
    @Autowired
    private ProductMapper productMapper;

    /**
     * 优惠券类型：满减
     */
    private static final String COUPON_TYPE_FIXED_AMOUNT = "FIXED_AMOUNT";

    /**
     * 优惠券类型：折扣
     */
    private static final String COUPON_TYPE_DISCOUNT = "DISCOUNT";

    /**
     * 活动类型：优惠券
     */
    private static final String ACTIVITY_TYPE_COUPON = "COUPON";

    /**
     * 活动类型：折扣
     */
    private static final String ACTIVITY_TYPE_DISCOUNT = "DISCOUNT";

    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 发布优惠券
     *
     * @param shopId           商铺ID
     * @param couponPublishVO  发布优惠券请求参数
     * @return 优惠券ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishCoupon(Long shopId, CouponPublishVO couponPublishVO) {
        // 1. 校验商铺归属（实际项目中可能需要从用户关联的shop表中查询）
        // 2. 校验优惠券类型
        if (!COUPON_TYPE_FIXED_AMOUNT.equals(couponPublishVO.getCouponType()) && !COUPON_TYPE_DISCOUNT.equals(couponPublishVO.getCouponType())) {
            throw new GlobalExceptionHandler.BusinessException(400, "无效的优惠券类型");
        }

        // 3. 校验满减券的使用门槛和优惠值
        if (COUPON_TYPE_FIXED_AMOUNT.equals(couponPublishVO.getCouponType())) {
            if (couponPublishVO.getThreshold().compareTo(BigDecimal.ZERO) <= 0) {
                throw new GlobalExceptionHandler.BusinessException(400, "满减券使用门槛必须大于0");
            }
            if (couponPublishVO.getValue().compareTo(couponPublishVO.getThreshold()) >= 0) {
                throw new GlobalExceptionHandler.BusinessException(400, "满减券优惠值必须小于使用门槛");
            }
        }

        // 4. 校验折扣券的折扣率
        if (COUPON_TYPE_DISCOUNT.equals(couponPublishVO.getCouponType())) {
            if (couponPublishVO.getValue().compareTo(new BigDecimal("0.1")) < 0 || couponPublishVO.getValue().compareTo(new BigDecimal("0.99")) > 0) {
                throw new GlobalExceptionHandler.BusinessException(400, "折扣券折扣率必须在0.1-0.99之间");
            }
            if (couponPublishVO.getThreshold().compareTo(BigDecimal.ZERO) != 0) {
                throw new GlobalExceptionHandler.BusinessException(400, "折扣券使用门槛必须为0");
            }
        }

        // 5. 校验时间范围
        LocalDateTime startTime = LocalDateTime.parse(couponPublishVO.getStartTime(), DATE_TIME_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(couponPublishVO.getEndTime(), DATE_TIME_FORMATTER);
        if (startTime.isAfter(endTime)) {
            throw new GlobalExceptionHandler.BusinessException(400, "生效时间晚于失效时间");
        }

        // 6. 校验剩余库存
        Integer remainNum = couponPublishVO.getRemainNum();
        if (remainNum == null || remainNum < 0 || remainNum > couponPublishVO.getTotalNum()) {
            remainNum = couponPublishVO.getTotalNum();
        }

        // 7. 校验商品ID列表（若有）
        String productIdsStr = null;
        if (couponPublishVO.getProductIds() != null && !couponPublishVO.getProductIds().isEmpty()) {
            // 校验商品是否属于当前商铺且已上架
            List<Long> productIds = couponPublishVO.getProductIds();
            for (Long productId : productIds) {
                Product product = productMapper.selectById(productId);
                if (product == null || product.getStatus() == 0 || !product.getShopId().equals(shopId)) {
                    throw new GlobalExceptionHandler.BusinessException(400, "商品ID " + productId + " 无效或不属于当前商铺");
                }
            }
            productIdsStr = String.join(",", productIds.stream().map(String::valueOf).collect(Collectors.toList()));
        }

        // 8. 构建优惠券实体
        Coupon coupon = new Coupon();
        coupon.setShopId(shopId);
        coupon.setCouponType(couponPublishVO.getCouponType());
        coupon.setThreshold(couponPublishVO.getThreshold());
        coupon.setValue(couponPublishVO.getValue());
        coupon.setTotalNum(couponPublishVO.getTotalNum());
        coupon.setRemainNum(remainNum);
        coupon.setStartTime(startTime);
        coupon.setEndTime(endTime);
        coupon.setProductIds(productIdsStr);
        coupon.setStatus(1); // 1-生效
        coupon.setCreateTime(LocalDateTime.now());

        // 9. 保存优惠券
        couponMapper.insert(coupon);

        // 10. 更新Redis缓存（实际项目中需要实现Redis缓存逻辑）

        return coupon.getId();
    }

    /**
     * 创建打折活动
     *
     * @param shopId                    商铺ID
     * @param discountActivityCreateVO  创建打折活动请求参数
     * @return 活动ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDiscountActivity(Long shopId, DiscountActivityCreateVO discountActivityCreateVO) {
        // 1. 校验商铺归属（实际项目中可能需要从用户关联的shop表中查询）
        // 2. 校验折扣率
        BigDecimal discountRate = discountActivityCreateVO.getDiscountRate();
        if (discountRate.compareTo(new BigDecimal("0.1")) < 0 || discountRate.compareTo(new BigDecimal("0.99")) > 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "折扣率必须在0.1-0.99之间");
        }

        // 3. 校验时间范围
        LocalDateTime startTime = LocalDateTime.parse(discountActivityCreateVO.getStartTime(), DATE_TIME_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(discountActivityCreateVO.getEndTime(), DATE_TIME_FORMATTER);
        if (startTime.isAfter(endTime)) {
            throw new GlobalExceptionHandler.BusinessException(400, "生效时间晚于失效时间");
        }

        // 4. 校验商品ID列表
        List<Long> productIds = discountActivityCreateVO.getProductIds();
        for (Long productId : productIds) {
            Product product = productMapper.selectById(productId);
            if (product == null || product.getStatus() == 0 || !product.getShopId().equals(shopId)) {
                throw new GlobalExceptionHandler.BusinessException(400, "商品ID " + productId + " 无效或不属于当前商铺");
            }
        }

        // 5. 构建打折活动实体
        DiscountActivity discountActivity = new DiscountActivity();
        discountActivity.setShopId(shopId);
        discountActivity.setActivityName(discountActivityCreateVO.getActivityName());
        discountActivity.setDiscountRate(discountRate);
        discountActivity.setStartTime(startTime);
        discountActivity.setEndTime(endTime);
        discountActivity.setStatus(1); // 1-生效
        discountActivity.setCreateTime(LocalDateTime.now());

        // 6. 保存打折活动
        discountActivityMapper.insert(discountActivity);

        // 7. 批量关联活动商品
        List<DiscountActivityProduct> activityProducts = new ArrayList<>();
        for (Long productId : productIds) {
            DiscountActivityProduct activityProduct = new DiscountActivityProduct();
            activityProduct.setActivityId(discountActivity.getId());
            activityProduct.setProductId(productId);
            activityProducts.add(activityProduct);
        }
        discountActivityProductMapper.batchInsert(activityProducts);

        // 8. 更新Redis缓存（实际项目中需要实现Redis缓存逻辑）

        return discountActivity.getId();
    }

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
    @Override
    public Page<Coupon> getMerchantCoupons(Long shopId, String couponType, Integer status, Integer pageNum, Integer pageSize) {
        Page<Coupon> page = new Page<>(pageNum, pageSize);
        return couponMapper.selectByShopId(page, shopId, status, couponType);
    }

    /**
     * 商家查询自家活动（打折活动）
     *
     * @param shopId  商铺ID
     * @param status  状态（生效/失效，可选）
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 打折活动列表（分页）
     */
    @Override
    public Page<DiscountActivity> getMerchantDiscountActivities(Long shopId, Integer status, Integer pageNum, Integer pageSize) {
        Page<DiscountActivity> page = new Page<>(pageNum, pageSize);
        return discountActivityMapper.selectByShopId(page, shopId, status);
    }

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
    @Override
    public Page<Coupon> getAdminCoupons(Long shopId, String couponType, String startTime, String endTime, Integer pageNum, Integer pageSize) {
        Page<Coupon> page = new Page<>(pageNum, pageSize);
        Map<String, Object> params = new HashMap<>();
        params.put("shopId", shopId);
        params.put("couponType", couponType);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return couponMapper.selectByAdmin(page, params);
    }

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
    @Override
    public Page<DiscountActivity> getAdminDiscountActivities(Long shopId, String startTime, String endTime, Integer pageNum, Integer pageSize) {
        Page<DiscountActivity> page = new Page<>(pageNum, pageSize);
        Map<String, Object> params = new HashMap<>();
        params.put("shopId", shopId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return discountActivityMapper.selectByAdmin(page, params);
    }

    /**
     * 客户查询可领取优惠券
     *
     * @param shopId   商铺ID（可选）
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 可领取优惠券列表（分页）
     */
    @Override
    public Page<Coupon> getAvailableCoupons(Long shopId, Integer pageNum, Integer pageSize) {
        // 查询生效中、库存>0的优惠券
        List<Coupon> validCoupons = couponMapper.selectValidCoupons(shopId);
        // 实际项目中需要实现分页查询，这里简化处理
        Page<Coupon> page = new Page<>(pageNum, pageSize);
        page.setRecords(validCoupons);
        page.setTotal(validCoupons.size());
        return page;
    }

    /**
     * 客户领取优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 领取结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> receiveCoupon(Long userId, Long couponId) {
        // 1. 查询优惠券信息
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "优惠券不存在");
        }

        // 2. 校验优惠券是否生效且在有效期内
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStatus() != 1 || now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new GlobalExceptionHandler.BusinessException(400, "优惠券已失效");
        }

        // 3. 校验优惠券库存
        if (coupon.getRemainNum() <= 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "优惠券已领完");
        }

        // 4. 校验用户是否已领取该优惠券
        int exists = userCouponMapper.checkUserCouponExists(userId, couponId);
        if (exists > 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "你已领取过该优惠券");
        }

        // 5. 扣减优惠券剩余库存
        int updateResult = couponMapper.decreaseRemainNum(couponId);
        if (updateResult == 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "领取失败，请重试");
        }

        // 6. 新增用户优惠券关联记录
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(1); // 1-未使用
        userCoupon.setReceiveTime(LocalDateTime.now());
        userCouponMapper.insert(userCoupon);

        // 7. 更新Redis缓存（实际项目中需要实现Redis缓存逻辑）

        // 8. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("couponId", couponId);
        result.put("coupon", coupon);
        result.put("receiveTime", userCoupon.getReceiveTime());

        return result;
    }

    /**
     * 查询客户已领优惠券
     *
     * @param userId   用户ID
     * @param status   状态（可选：1-未使用，2-已使用，3-已过期）
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 客户优惠券列表（分页）
     */
    @Override
    public Page<UserCoupon> getMyCoupons(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<UserCoupon> page = new Page<>(pageNum, pageSize);
        return userCouponMapper.selectByUserId(page, userId, status);
    }

    /**
     * 检查活动有效性
     *
     * @param activityId 活动ID
     * @param activityType 活动类型（COUPON/DISCOUNT）
     * @return 活动是否有效
     */
    @Override
    public boolean checkActivityValid(Long activityId, String activityType) {
        LocalDateTime now = LocalDateTime.now();
        if (ACTIVITY_TYPE_COUPON.equals(activityType)) {
            Coupon coupon = couponMapper.selectById(activityId);
            return coupon != null && coupon.getStatus() == 1 && now.isAfter(coupon.getStartTime()) && now.isBefore(coupon.getEndTime());
        } else if (ACTIVITY_TYPE_DISCOUNT.equals(activityType)) {
            DiscountActivity activity = discountActivityMapper.selectById(activityId);
            return activity != null && activity.getStatus() == 1 && now.isAfter(activity.getStartTime()) && now.isBefore(activity.getEndTime());
        }
        return false;
    }

    /**
     * 检查优惠券可用性
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @param shopId   商铺ID
     * @param totalAmount 订单总金额
     * @return 优惠券是否可用
     */
    @Override
    public boolean checkCouponUsable(Long userId, Long couponId, Long shopId, BigDecimal totalAmount) {
        // 1. 查询优惠券信息
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            return false;
        }

        // 2. 校验优惠券是否在有效期内
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            return false;
        }

        // 3. 校验优惠券归属
        if (!coupon.getShopId().equals(shopId)) {
            return false;
        }

        // 4. 校验使用门槛
        if (coupon.getThreshold().compareTo(totalAmount) > 0) {
            return false;
        }

        // 5. 校验用户是否已领取且未使用该优惠券
        // 实际项目中需要查询user_coupon表，这里简化处理
        return true;
    }

    /**
     * 计算优惠金额
     *
     * @param totalAmount 订单总金额
     * @param couponId    优惠券ID（可选）
     * @param productId   商品ID（可选，用于打折活动）
     * @return 优惠金额
     */
    @Override
    public BigDecimal calculateDiscountAmount(BigDecimal totalAmount, Long couponId, Long productId) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        // 1. 计算优惠券优惠
        if (couponId != null) {
            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon != null) {
                if (COUPON_TYPE_FIXED_AMOUNT.equals(coupon.getCouponType())) {
                    // 满减券
                    if (totalAmount.compareTo(coupon.getThreshold()) >= 0) {
                        discountAmount = coupon.getValue();
                    }
                } else if (COUPON_TYPE_DISCOUNT.equals(coupon.getCouponType())) {
                    // 折扣券
                    discountAmount = totalAmount.subtract(totalAmount.multiply(coupon.getValue()));
                }
            }
        }

        // 2. 计算打折活动优惠（这里简化处理，实际项目中需要查询商品是否参与打折活动）
        // ...

        return discountAmount;
    }

    /**
     * 使用优惠券（下单时）
     *
     * @param userCouponId 用户优惠券ID
     * @param orderId      订单ID
     * @return 使用结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean useCoupon(Long userCouponId, Long orderId) {
        // 更新优惠券状态为已使用
        int updateResult = userCouponMapper.useCoupon(userCouponId, orderId);
        return updateResult > 0;
    }

}