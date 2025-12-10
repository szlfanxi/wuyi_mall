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
 * 商品价格变动日志实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("product_price_log")
public class ProductPriceLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 旧价格
     */
    @TableField(value = "old_price")
    private BigDecimal oldPrice;

    /**
     * 新价格
     */
    @TableField(value = "new_price")
    private BigDecimal newPrice;

    /**
     * 操作人ID
     */
    @TableField(value = "operate_user_id")
    private Long operateUserId;

    /**
     * 操作时间
     */
    @TableField(value = "operate_time")
    private LocalDateTime operateTime;

    /**
     * 操作类型，如PRICE_MODIFY
     */
    @TableField(value = "operate_type")
    private String operateType;

}
