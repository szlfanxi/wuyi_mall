package com.wuyi.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.MerchantApply;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 商家申请服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface MerchantApplyService {

    /**
     * 提交商家入驻申请
     *
     * @param merchantApply 商家申请信息
     * @return 申请结果，包含applyId
     */
    Map<String, Object> submitMerchantApply(MerchantApply merchantApply);

    /**
     * 查询商家申请列表（分页）
     *
     * @param status     状态：0-待审核，1-已通过，2-已拒绝
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 商家申请列表
     */
    Page<MerchantApply> getMerchantApplyList(Integer status, LocalDateTime startTime, LocalDateTime endTime, Integer pageNum, Integer pageSize);

    /**
     * 根据ID查询商家申请
     *
     * @param id 申请ID
     * @return 商家申请信息
     */
    MerchantApply getMerchantApplyById(Long id);

}
