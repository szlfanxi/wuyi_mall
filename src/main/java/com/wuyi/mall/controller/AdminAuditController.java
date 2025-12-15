package com.wuyi.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.AuditLog;
import com.wuyi.mall.service.AdminAuditService;
import com.wuyi.mall.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 管理员审核Controller
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/admin")
public class AdminAuditController {

    @Autowired
    private AdminAuditService adminAuditService;

    /**
     * 审核商家入驻申请
     * 前置条件：管理员已登录（JWT解析user_id，user.role_type=3），请求头携带有效Authorization
     *
     * @param authentication 认证信息
     * @param applyId       申请ID
     * @param auditResult   审核结果：0-拒绝，1-通过
     * @param remark        审核备注
     * @return 审核结果
     */
    @PostMapping("/audit/merchant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil.Result<Map<String, Object>> auditMerchantApply(
            Authentication authentication,
            @RequestParam Long applyId,
            @RequestParam Integer auditResult,
            @RequestParam String remark) {
        // 从认证信息中获取审核人ID
        Long auditUserId = Long.parseLong(authentication.getPrincipal().toString());

        Map<String, Object> result = adminAuditService.auditMerchantApply(applyId, auditResult, remark, auditUserId);
        return ResultUtil.success(result, "审核操作成功");
    }

    /**
     * 审批商铺创建申请
     * 前置条件：管理员已登录，入参包含applyId、auditResult、remark（拒绝必填）
     *
     * @param authentication 认证信息
     * @param applyId       申请ID
     * @param auditResult   审核结果：0-拒绝，1-通过
     * @param remark        审核备注
     * @return 审批结果
     */
    @PostMapping("/audit/shop")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil.Result<Map<String, Object>> auditShopApply(
            Authentication authentication,
            @RequestParam Long applyId,
            @RequestParam Integer auditResult,
            @RequestParam String remark) {
        // 从认证信息中获取审核人ID
        Long auditUserId = Long.parseLong(authentication.getPrincipal().toString());

        Map<String, Object> result = adminAuditService.auditShopApply(applyId, auditResult, remark, auditUserId);
        return ResultUtil.success(result, "审批操作成功");
    }

    /**
     * 更新商铺状态
     * 前置条件：管理员已登录，入参shopId、status（1-启用，0-禁用）
     *
     * @param authentication 认证信息
     * @param shopId        商铺ID
     * @param status        状态：1-启用，0-禁用
     * @param remark        操作备注
     * @return 操作结果
     */
    @PostMapping("/shops/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil.Result<Map<String, Object>> updateShopStatus(
            Authentication authentication,
            @RequestParam Long shopId,
            @RequestParam Integer status,
            @RequestParam String remark) {
        // 从认证信息中获取操作人ID
        Long operatorId = Long.parseLong(authentication.getPrincipal().toString());

        Map<String, Object> result = adminAuditService.updateShopStatus(shopId, status, remark, operatorId);
        return ResultUtil.success(result, "商铺状态更新成功");
    }

    /**
     * 查询审核日志列表（分页）
     * 前置条件：管理员已登录
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
    @GetMapping("/audit/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil.Result<Page<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String operateType,
            @RequestParam(required = false) Integer operateResult,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(required = false) Long operateUserId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<AuditLog> auditLogs = adminAuditService.getAuditLogs(operateType, operateResult, startTime, endTime, operateUserId, pageNum, pageSize);
        return ResultUtil.success(auditLogs);
    }

}
