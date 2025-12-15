package com.wuyi.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.ShopApply;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 商铺申请服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface ShopApplyService {

    /**
     * 提交商铺创建申请
     *
     * @param shopApply 商铺申请信息
     * @return 申请结果，包含shopApplyId
     */
    Map<String, Object> submitShopApply(ShopApply shopApply);

    /**
     * 查询商家的商铺申请列表（分页）
     *
     * @param merchantId 商家ID
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 商铺申请列表
     */
    Page<ShopApply> getShopApplyListByMerchant(Long merchantId, Integer pageNum, Integer pageSize);

    /**
     * 查询商铺申请列表（分页）
     *
     * @param status     状态：0-待审核，1-已通过，2-已拒绝
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 商铺申请列表
     */
    Page<ShopApply> getShopApplyList(Integer status, LocalDateTime startTime, LocalDateTime endTime, Integer pageNum, Integer pageSize);

    /**
     * 根据ID查询商铺申请
     *
     * @param id 申请ID
     * @return 商铺申请信息
     */
    ShopApply getShopApplyById(Long id);

}
