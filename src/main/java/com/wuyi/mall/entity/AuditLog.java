package com.wuyi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审核日志实体类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@TableName("audit_log")
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作类型：MERCHANT_AUDIT-商家审核，SHOP_AUDIT-商铺审核，SHOP_STATUS_UPDATE-商铺状态更新
     */
    @TableField(value = "operate_type")
    private String operateType;

    /**
     * 目标ID（申请ID/商铺ID）
     */
    @TableField(value = "target_id")
    private Long targetId;

    /**
     * 操作人ID
     */
    @TableField(value = "operate_user_id")
    private Long operateUserId;

    /**
     * 操作结果：0-拒绝，1-通过/启用，2-禁用
     */
    @TableField(value = "operate_result")
    private Integer operateResult;

    /**
     * 操作备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 操作时间
     */
    @TableField(value = "operate_time")
    private LocalDateTime operateTime;
}
