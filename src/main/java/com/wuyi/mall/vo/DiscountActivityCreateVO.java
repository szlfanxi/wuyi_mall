package com.wuyi.mall.vo;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建打折活动请求VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class DiscountActivityCreateVO {

    /**
     * 商铺ID（必填，归属当前商家）
     */
    @NotNull(message = "商铺ID不能为空")
    private Long shopId;

    /**
     * 活动名称（必填，1-50字）
     */
    @NotBlank(message = "活动名称不能为空")
    @Size(min = 1, max = 50, message = "活动名称长度必须在1-50字之间")
    private String activityName;

    /**
     * 折扣率（必填，0.1-0.99）
     */
    @NotNull(message = "折扣率不能为空")
    @DecimalMin(value = "0.1", message = "折扣率必须大于等于0.1")
    @DecimalMax(value = "0.99", message = "折扣率必须小于等于0.99")
    private BigDecimal discountRate;

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
     * 参与商品ID列表（必填≥1，均为该商铺上架商品）
     */
    @NotNull(message = "参与商品ID列表不能为空")
    @Size(min = 1, message = "参与商品数量必须大于等于1")
    private List<Long> productIds;

}