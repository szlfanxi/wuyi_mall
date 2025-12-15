package com.wuyi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 活动商品关联实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("discount_activity_product")
public class DiscountActivityProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动ID
     */
    @TableField(value = "activity_id")
    private Long activityId;

    /**
     * 商品ID
     */
    @TableField(value = "product_id")
    private Long productId;

}