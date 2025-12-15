package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.ShopApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 商铺申请Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface ShopApplyMapper extends BaseMapper<ShopApply> {

    /**
     * 根据商家ID查询商铺申请列表（分页）
     *
     * @param page       分页参数
     * @param merchantId 商家ID
     * @return 商铺申请列表
     */
    Page<ShopApply> selectByMerchantId(Page<ShopApply> page, @Param("merchantId") Long merchantId);

    /**
     * 根据状态查询商铺申请列表（分页）
     *
     * @param page       分页参数
     * @param status     状态：0-待审核，1-已通过，2-已拒绝
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 商铺申请列表
     */
    Page<ShopApply> selectByStatus(Page<ShopApply> page, @Param("status") Integer status, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 检查商铺名称是否已存在
     *
     * @param shopName 商铺名称
     * @return 存在数量
     */
    int checkShopNameExists(@Param("shopName") String shopName);

    /**
     * 根据商家ID查询待审核的商铺申请数量
     *
     * @param merchantId 商家ID
     * @return 待审核申请数量
     */
    int countPendingByMerchantId(@Param("merchantId") Long merchantId);

}
