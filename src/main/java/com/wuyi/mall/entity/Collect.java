package com.wuyi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("collect")
public class Collect implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，外键关联user表
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 商品ID，外键关联product表
     */
    @TableField(value = "product_id")
    private Long productId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}