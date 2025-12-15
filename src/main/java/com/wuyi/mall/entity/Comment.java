package com.wuyi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品评价实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评价ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单详情ID，唯一索引
     */
    @TableField(value = "order_item_id")
    private Long orderItemId;

    /**
     * 商品ID
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评分，1-5分
     */
    private Integer score;

    /**
     * 评价图片URL，逗号分隔
     */
    private String images;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

}