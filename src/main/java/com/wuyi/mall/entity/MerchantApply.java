package com.wuyi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商家申请表实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("merchant_apply")
public class MerchantApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商家名称
     */
    @TableField(value = "merchant_name")
    private String merchantName;

    /**
     * 法人姓名
     */
    @TableField(value = "legal_person")
    private String legalPerson;

    /**
     * 法人身份证号（脱敏存储）
     */
    @TableField(value = "id_card")
    private String idCard;

    /**
     * 营业执照编号
     */
    @TableField(value = "business_license")
    private String businessLicense;

    /**
     * 联系电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 联系邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 商家地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 对公银行卡号（脱敏存储）
     */
    @TableField(value = "bank_account")
    private String bankAccount;

    /**
     * 开户银行
     */
    @TableField(value = "bank_name")
    private String bankName;

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
