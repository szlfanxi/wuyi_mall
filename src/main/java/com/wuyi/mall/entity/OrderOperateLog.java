package com.wuyi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单操作日志实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("order_operate_log")
public class OrderOperateLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID，外键关联order表
     */
    @TableField(value = "order_id")
    private Long orderId;

    /**
     * 操作人ID，外键关联user表
     */
    @TableField(value = "operate_user_id")
    private Long operateUserId;

    /**
     * 操作类型，枚举值：CONFIRM、PREPARE、DELIVER、CANCEL_BY_CUSTOMER、CANCEL_BY_MERCHANT
     */
    @TableField(value = "operate_type")
    private String operateType;

    /**
     * 操作前状态，对应order.status
     */
    @TableField(value = "before_status")
    private Integer beforeStatus;

    /**
     * 操作后状态，对应order.status
     */
    @TableField(value = "after_status")
    private Integer afterStatus;

    /**
     * 操作时间
     */
    @TableField(value = "operate_time")
    private LocalDateTime operateTime;

}