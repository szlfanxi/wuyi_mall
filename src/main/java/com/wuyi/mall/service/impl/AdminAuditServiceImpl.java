package com.wuyi.mall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.AuditLog;
import com.wuyi.mall.entity.MerchantApply;
import com.wuyi.mall.entity.ShopApply;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.AuditLogMapper;
import com.wuyi.mall.mapper.MerchantApplyMapper;
import com.wuyi.mall.mapper.ShopApplyMapper;
import com.wuyi.mall.service.AdminAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 管理员审核服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class AdminAuditServiceImpl implements AdminAuditService {

    @Autowired
    private MerchantApplyMapper merchantApplyMapper;

    @Autowired
    private ShopApplyMapper shopApplyMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 审核商家入驻申请
     *
     * @param applyId       申请ID
     * @param auditResult   审核结果：0-拒绝，1-通过
     * @param remark        审核备注
     * @param auditUserId   审核人ID
     * @return 审核结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> auditMerchantApply(Long applyId, Integer auditResult, String remark, Long auditUserId) {
        // 查询商家申请
        MerchantApply merchantApply = merchantApplyMapper.selectById(applyId);
        if (merchantApply == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "商家申请不存在");
        }

        // 校验申请状态
        if (merchantApply.getStatus() != 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "申请已审核，无法重复操作");
        }

        // 校验拒绝时备注不能为空
        if (auditResult == 0 && (remark == null || remark.trim().isEmpty())) {
            throw new GlobalExceptionHandler.BusinessException(400, "拒绝时备注不能为空");
        }

        // 更新商家申请状态
        merchantApply.setStatus(auditResult);
        merchantApply.setAuditTime(LocalDateTime.now());
        merchantApply.setAuditUserId(auditUserId);
        merchantApply.setRemark(remark);
        merchantApplyMapper.updateById(merchantApply);

        // 如果审核通过，创建商家用户
        if (auditResult == 1) {
            // 创建商家用户（这里简化处理，实际项目中需要调用用户服务创建用户）
            // userService.createMerchantUser(merchantApply);
        }

        // 记录审核日志
        AuditLog auditLog = new AuditLog();
        auditLog.setOperateType("MERCHANT_AUDIT");
        auditLog.setTargetId(applyId);
        auditLog.setOperateUserId(auditUserId);
        auditLog.setOperateResult(auditResult);
        auditLog.setRemark(remark);
        auditLog.setOperateTime(LocalDateTime.now());
        auditLogMapper.insert(auditLog);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("applyId", applyId);
        result.put("auditResult", auditResult);
        return result;
    }

    /**
     * 审批商铺创建申请
     *
     * @param applyId       申请ID
     * @param auditResult   审核结果：0-拒绝，1-通过
     * @param remark        审核备注
     * @param auditUserId   审核人ID
     * @return 审批结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> auditShopApply(Long applyId, Integer auditResult, String remark, Long auditUserId) {
        // 查询商铺申请
        ShopApply shopApply = shopApplyMapper.selectById(applyId);
        if (shopApply == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "商铺申请不存在");
        }

        // 校验申请状态
        if (shopApply.getStatus() != 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "申请已审批，无法重复操作");
        }

        // 校验拒绝时备注不能为空
        if (auditResult == 0 && (remark == null || remark.trim().isEmpty())) {
            throw new GlobalExceptionHandler.BusinessException(400, "拒绝时备注不能为空");
        }

        // 更新商铺申请状态
        shopApply.setStatus(auditResult);
        shopApply.setAuditTime(LocalDateTime.now());
        shopApply.setAuditUserId(auditUserId);
        shopApply.setRemark(remark);
        shopApplyMapper.updateById(shopApply);

        // 如果审批通过，创建商铺
        if (auditResult == 1) {
            // 创建商铺（这里简化处理，实际项目中需要调用商铺服务创建商铺）
            // shopService.createShop(shopApply);
        }

        // 记录审核日志
        AuditLog auditLog = new AuditLog();
        auditLog.setOperateType("SHOP_AUDIT");
        auditLog.setTargetId(applyId);
        auditLog.setOperateUserId(auditUserId);
        auditLog.setOperateResult(auditResult);
        auditLog.setRemark(remark);
        auditLog.setOperateTime(LocalDateTime.now());
        auditLogMapper.insert(auditLog);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("applyId", applyId);
        result.put("auditResult", auditResult);
        return result;
    }

    /**
     * 更新商铺状态
     *
     * @param shopId        商铺ID
     * @param status        状态：1-启用，0-禁用
     * @param remark        操作备注
     * @param operatorId    操作人ID
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateShopStatus(Long shopId, Integer status, String remark, Long operatorId) {
        // 校验商铺ID
        if (shopId == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "商铺ID不能为空");
        }

        // 校验状态值
        if (status != 0 && status != 1) {
            throw new GlobalExceptionHandler.BusinessException(400, "状态值无效，只能是0或1");
        }

        // 校验禁用时备注不能为空
        if (status == 0 && (remark == null || remark.trim().isEmpty())) {
            throw new GlobalExceptionHandler.BusinessException(400, "禁用商铺时备注不能为空");
        }

        // 更新商铺状态（这里简化处理，实际项目中需要调用商铺服务更新状态）
        // shopService.updateShopStatus(shopId, status);

        // 如果是禁用商铺，下架其商品
        if (status == 0) {
            // productService.offlineProductsByShopId(shopId);
        }

        // 记录操作日志
        AuditLog auditLog = new AuditLog();
        auditLog.setOperateType("SHOP_STATUS_UPDATE");
        auditLog.setTargetId(shopId);
        auditLog.setOperateUserId(operatorId);
        auditLog.setOperateResult(status == 1 ? 1 : 2); // 1-启用，2-禁用
        auditLog.setRemark(remark);
        auditLog.setOperateTime(LocalDateTime.now());
        auditLogMapper.insert(auditLog);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("shopId", shopId);
        result.put("status", status);
        return result;
    }

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
    @Override
    public Page<AuditLog> getAuditLogs(String operateType, Integer operateResult, LocalDateTime startTime, LocalDateTime endTime, Long operateUserId, Integer pageNum, Integer pageSize) {
        Page<AuditLog> page = new Page<>(pageNum, pageSize);
        return auditLogMapper.selectAuditLogs(page, operateType, operateResult, startTime, endTime, operateUserId);
    }

    /**
     * 生成随机密码
     *
     * @return 随机密码
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
