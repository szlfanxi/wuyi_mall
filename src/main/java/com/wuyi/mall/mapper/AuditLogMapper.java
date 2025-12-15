package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 审核日志Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {

    /**
     * 查询审核日志列表（分页）
     *
     * @param page          分页参数
     * @param operateType   操作类型：MERCHANT_AUDIT-商家审核，SHOP_AUDIT-商铺审核，SHOP_STATUS_UPDATE-商铺状态更新
     * @param operateResult 操作结果：0-拒绝，1-通过/启用，2-禁用
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param operateUserId 操作人ID
     * @return 审核日志列表
     */
    Page<AuditLog> selectAuditLogs(Page<AuditLog> page, @Param("operateType") String operateType, @Param("operateResult") Integer operateResult, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("operateUserId") Long operateUserId);

    /**
     * 根据目标ID和操作类型查询审核日志
     *
     * @param targetId     目标ID（申请ID/商铺ID）
     * @param operateType  操作类型
     * @return 审核日志列表
     */
    AuditLog selectByTargetIdAndType(@Param("targetId") Long targetId, @Param("operateType") String operateType);

}
