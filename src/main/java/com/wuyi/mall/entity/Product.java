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
 * 商品实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品原价
     */
    private BigDecimal price;

    /**
     * 商品当前价格
     */
    @TableField(value = "current_price")
    private BigDecimal currentPrice;

    /**
     * 商品库存数量
     */
    @TableField(value = "stock_num")
    private Integer stockNum;

    /**
     * 所属店铺ID
     */
    @TableField(value = "shop_id")
    private Long shopId;

    /**
     * 商品状态，0-下架，1-上架
     */
    private Integer status;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 分类ID
     */
    @TableField(value = "category_id")
    private Long categoryId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除，0-正常，1-删除
     */
    @TableField(value = "is_deleted")
    private Integer deleted;

    /**
     * 版本号，用于乐观锁
     */
    @TableField(value = "version")
    private Integer version;

    /**
     * 平均评分，保留1位小数
     */
    @TableField(value = "average_score")
    private BigDecimal averageScore;

    /**
     * 评价数量
     */
    @TableField(value = "comment_count")
    private Integer commentCount;

}