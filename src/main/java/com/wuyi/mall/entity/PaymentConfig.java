package com.wuyi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 支付配置实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("payment_config")
public class PaymentConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 支付方式（ALIPAY/WECHAT/CREDIT_CARD）
     */
    @TableField(value = "pay_type")
    private String payType;

    /**
     * 应用ID
     */
    @TableField(value = "app_id")
    private String appId;

    /**
     * 商户ID
     */
    @TableField(value = "merchant_id")
    private String merchantId;

    /**
     * 私钥（加密存储）
     */
    @TableField(value = "private_key")
    private String privateKey;

    /**
     * 公钥
     */
    @TableField(value = "public_key")
    private String publicKey;

    /**
     * 支付网关URL
     */
    @TableField(value = "gateway_url")
    private String gatewayUrl;

    /**
     * 同步回调URL
     */
    @TableField(value = "return_url")
    private String returnUrl;

    /**
     * 异步通知URL
     */
    @TableField(value = "notify_url")
    private String notifyUrl;

    /**
     * 状态（0：禁用，1：启用）
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
