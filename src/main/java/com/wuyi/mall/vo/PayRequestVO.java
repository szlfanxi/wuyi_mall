package com.wuyi.mall.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发起支付请求VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class PayRequestVO {

    /**
     * 订单ID（必填）
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 支付方式（必填：ALIPAY/WECHAT/CREDIT_CARD）
     */
    @NotBlank(message = "支付方式不能为空")
    private String payType;

    /**
     * 客户端IP（必填，用于支付风控）
     */
    @NotBlank(message = "客户端IP不能为空")
    private String clientIp;

}
