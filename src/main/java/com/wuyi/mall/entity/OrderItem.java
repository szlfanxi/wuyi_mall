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
 * 订单详情实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单详情ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID，外键关联order表
     */
    @TableField(value = "order_id")
    private Long orderId;

    /**
     * 商品ID，外键关联product表
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 商品价格（快照）
     */
    private BigDecimal price;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}