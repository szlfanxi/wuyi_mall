package com.wuyi.mall.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 领取优惠券请求VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class CouponReceiveVO {

    /**
     * 优惠券ID
     */
    @NotNull(message = "优惠券ID不能为空")
    private Long couponId;

}