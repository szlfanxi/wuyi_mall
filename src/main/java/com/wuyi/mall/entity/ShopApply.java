package com.wuyi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商铺申请表实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("shop_apply")
public class ShopApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商家ID（关联user表）
     */
    @TableField(value = "merchant_id")
    private Long merchantId;

    /**
     * 商铺名称
     */
    @TableField(value = "shop_name")
    private String shopName;

    /**
     * 商铺描述
     */
    @TableField(value = "shop_desc")
    private String shopDesc;

    /**
     * 商铺分类ID
     */
    @TableField(value = "category_id")
    private Long categoryId;

    /**
     * 商铺LOGO URL
     */
    @TableField(value = "logo_url")
    private String logoUrl;

    /**
     * 商铺联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;

    /**
     * 状态：0-待审核，1-已通过，2-已拒绝
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 审核时间
     */
    @TableField(value = "audit_time")
    private LocalDateTime auditTime;

    /**
     * 审核人ID
     */
    @TableField(value = "audit_user_id")
    private Long auditUserId;

    /**
     * 审核备注
     */
    @TableField(value = "remark")
    private String remark;
}
