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
 * 打折活动实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("discount_activity")
public class DiscountActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属商铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 活动名称
     */
    @TableField(value = "activity_name")
    private String activityName;

    /**
     * 折扣率（如0.8=8折）
     */
    @TableField(value = "discount_rate")
    private BigDecimal discountRate;

    /**
     * 生效时间
     */
    @TableField(value = "start_time")
    private LocalDateTime startTime;

    /**
     * 失效时间
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    /**
     * 状态：1-生效，0-失效
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

}