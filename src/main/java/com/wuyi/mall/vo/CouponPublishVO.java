package com.wuyi.mall.vo;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发布优惠券请求VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class CouponPublishVO {

    /**
     * 商铺ID（必填，归属当前商家）
     */
    @NotNull(message = "商铺ID不能为空")
    private Long shopId;

    /**
     * 优惠券类型（必填：满减/FIXED_AMOUNT、折扣/DISCOUNT）
     */
    @NotBlank(message = "优惠券类型不能为空")
    private String couponType;

    /**
     * 使用门槛（满减时必填，如满100可用；折扣券填0）
     */
    @NotNull(message = "使用门槛不能为空")
    @DecimalMin(value = "0.00", message = "使用门槛必须大于等于0")
    private BigDecimal threshold;

    /**
     * 优惠值（满减填金额，折扣填折扣率如0.8=8折，必填>0）
     */
    @NotNull(message = "优惠值不能为空")
    @DecimalMin(value = "0.01", message = "优惠值必须大于0")
    private BigDecimal value;

    /**
     * 总库存（必填≥1）
     */
    @NotNull(message = "总库存不能为空")
    @Min(value = 1, message = "总库存必须大于等于1")
    private Integer totalNum;

    /**
     * 剩余库存（默认等于totalNum）
     */
    private Integer remainNum;

    /**
     * 生效时间（必填，格式yyyy-MM-dd HH:mm:ss）
     */
    @NotBlank(message = "生效时间不能为空")
    private String startTime;

    /**
     * 失效时间（必填，晚于startTime）
     */
    @NotBlank(message = "失效时间不能为空")
    private String endTime;

    /**
     * 适用商品ID（可选，空则全店商品可用；非空则仅这些商品可用，且需归属当前商铺）
     */
    private List<Long> productIds;

}