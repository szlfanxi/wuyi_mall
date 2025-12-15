package com.wuyi.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.AuditLog;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 管理员审核服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface AdminAuditService {

    /**
     * 审核商家入驻申请
     *
     * @param applyId       申请ID
     * @param auditResult   审核结果：0-拒绝，1-通过
     * @param remark        审核备注
     * @param auditUserId   审核人ID
     * @return 审核结果
     */
    Map<String, Object> auditMerchantApply(Long applyId, Integer auditResult, String remark, Long auditUserId);

    /**
     * 审批商铺创建申请
     *
     * @param applyId       申请ID
     * @param auditResult   审核结果：0-拒绝，1-通过
     * @param remark        审核备注
     * @param auditUserId   审核人ID
     * @return 审批结果
     */
    Map<String, Object> auditShopApply(Long applyId, Integer auditResult, String remark, Long auditUserId);

    /**
     * 更新商铺状态
     *
     * @param shopId        商铺ID
     * @param status        状态：1-启用，0-禁用
     * @param remark        操作备注
     * @param operatorId    操作人ID
     * @return 操作结果
     */
    Map<String, Object> updateShopStatus(Long shopId, Integer status, String remark, Long operatorId);

    /**
     * 查询审核日志列表（分页）
     *
     * @param operateType   操作类型：MERCHANT_AUDIT-商家审核，SHOP_AUDIT-商铺审核，SHOP_STATUS_UPDATE-商铺状态更新
     * @param operateResult 操作结果：0-拒绝，1-通过/启用，2-禁用
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param operateUserId 操作人ID
     * @param pageNum       页码
     * @param pageSize      每页条数
     * @return 审核日志列表
     */
    Page<AuditLog> getAuditLogs(String operateType, Integer operateResult, LocalDateTime startTime, LocalDateTime endTime, Long operateUserId, Integer pageNum, Integer pageSize);

}
