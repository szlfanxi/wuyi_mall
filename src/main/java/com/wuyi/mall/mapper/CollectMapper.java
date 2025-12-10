package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyi.mall.entity.Collect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收藏Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface CollectMapper extends BaseMapper<Collect> {

    /**
     * 根据用户ID和商品ID查询收藏记录
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 收藏记录
     */
    Collect selectByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 批量新增收藏记录
     *
     * @param collects 收藏记录列表
     * @return 新增数量
     */
    int batchInsert(@Param("collects") List<Collect> collects);

    /**
     * 批量查询用户的收藏记录
     *
     * @param userId     用户ID
     * @param productIds 商品ID列表
     * @return 收藏记录列表
     */
    List<Collect> selectByUserIdAndProductIds(@Param("userId") Long userId, @Param("productIds") List<Long> productIds);

}