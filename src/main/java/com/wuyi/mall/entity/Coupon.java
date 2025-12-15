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
 * 优惠券实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 优惠券ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属商铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 优惠券类型：满减/FIXED_AMOUNT、折扣/DISCOUNT
     */
    @TableField(value = "coupon_type")
    private String couponType;

    /**
     * 使用门槛（满减时必填，如满100可用；折扣券填0）
     */
    private BigDecimal threshold;

    /**
     * 优惠值（满减填金额，折扣填折扣率如0.8=8折）
     */
    private BigDecimal value;

    /**
     * 总库存
     */
    @TableField(value = "total_num")
    private Integer totalNum;

    /**
     * 剩余库存
     */
    @TableField(value = "remain_num")
    private Integer remainNum;

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
     * 适用商品ID列表（逗号分隔，空则全店商品可用）
     */
    @TableField(value = "product_ids")
    private String productIds;

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