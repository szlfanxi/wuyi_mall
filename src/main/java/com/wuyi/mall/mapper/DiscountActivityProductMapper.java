package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyi.mall.entity.DiscountActivityProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动商品关联Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface DiscountActivityProductMapper extends BaseMapper<DiscountActivityProduct> {

    /**
     * 批量新增活动商品关联
     *
     * @param list 活动商品关联列表
     * @return 新增数量
     */
    int batchInsert(@Param("list") List<DiscountActivityProduct> list);

    /**
     * 按活动ID查询关联的商品ID列表
     *
     * @param activityId 活动ID
     * @return 商品ID列表
     */
    List<Long> selectProductIdsByActivityId(@Param("activityId") Long activityId);

    /**
     * 按活动ID删除所有关联的商品
     *
     * @param activityId 活动ID
     * @return 删除数量
     */
    int deleteByActivityId(@Param("activityId") Long activityId);

    /**
     * 检查商品是否已关联到活动
     *
     * @param activityId 活动ID
     * @param productId  商品ID
     * @return 1表示已关联，0表示未关联
     */
    int checkProductInActivity(@Param("activityId") Long activityId, @Param("productId") Long productId);

}