package com.wuyi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户优惠券关联实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("user_coupon")
public class UserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 优惠券ID
     */
    @TableField(value = "coupon_id")
    private Long couponId;

    /**
     * 状态：1-未使用，2-已使用，3-已过期
     */
    private Integer status;

    /**
     * 领取时间
     */
    @TableField(value = "receive_time")
    private LocalDateTime receiveTime;

    /**
     * 使用时间
     */
    @TableField(value = "use_time")
    private LocalDateTime useTime;

    /**
     * 关联订单ID
     */
    @TableField(value = "order_id")
    private Long orderId;

}