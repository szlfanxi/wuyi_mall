package com.wuyi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("payment_record")
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID，外键关联order表
     */
    @TableField(value = "order_id")
    private Long orderId;

    /**
     * 订单编号
     */
    @TableField(value = "order_no")
    private String orderNo;

    /**
     * 用户ID，外键关联user表
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 支付方式（ALIPAY/WECHAT/CREDIT_CARD）
     */
    @TableField(value = "pay_type")
    private String payType;

    /**
     * 支付金额
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 支付状态（0：未支付，1：支付成功，2：支付失败，3：支付超时）
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 第三方支付流水号
     */
    @TableField(value = "trade_no")
    private String tradeNo;

    /**
     * 客户端IP
     */
    @TableField(value = "client_ip")
    private String clientIp;

    /**
     * 支付参数（JSON格式）
     */
    @TableField(value = "pay_params")
    private String payParams;

    /**
     * 回调参数（JSON格式）
     */
    @TableField(value = "callback_params")
    private String callbackParams;

    /**
     * 回调时间
     */
    @TableField(value = "callback_time")
    private LocalDateTime callbackTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    @TableField(value = "pay_time")
    private LocalDateTime payTime;
}
