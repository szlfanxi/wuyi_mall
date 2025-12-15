package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.DiscountActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 打折活动Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface DiscountActivityMapper extends BaseMapper<DiscountActivity> {

    /**
     * 按商铺ID和状态查询打折活动（分页）
     *
     * @param page     分页参数
     * @param shopId   商铺ID
     * @param status   状态（可选）
     * @return 打折活动列表
     */
    Page<DiscountActivity> selectByShopId(Page<DiscountActivity> page, @Param("shopId") Long shopId, @Param("status") Integer status);

    /**
     * 按状态和时间查询有效打折活动（用于缓存）
     *
     * @param shopId 商铺ID（可选）
     * @return 打折活动列表
     */
    java.util.List<DiscountActivity> selectValidDiscountActivities(@Param("shopId") Long shopId);

    /**
     * 管理员查询所有打折活动（分页，支持多条件筛选）
     *
     * @param page   分页参数
     * @param params 查询参数，包含shopId、时间范围等
     * @return 打折活动列表
     */
    Page<DiscountActivity> selectByAdmin(Page<DiscountActivity> page, @Param("params") Map<String, Object> params);

    /**
     * 根据ID查询打折活动（包含商品信息）
     *
     * @param activityId 活动ID
     * @return 打折活动信息
     */
    DiscountActivity selectWithProductInfo(@Param("id") Long activityId);

}