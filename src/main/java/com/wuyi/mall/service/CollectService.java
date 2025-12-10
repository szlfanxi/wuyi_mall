package com.wuyi.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyi.mall.entity.Collect;
import com.wuyi.mall.vo.CollectBatchResultVO;

import java.util.List;

/**
 * 收藏服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface CollectService extends IService<Collect> {

    /**
     * 从购物车批量添加到收藏
     *
     * @param userId  用户ID
     * @param cartIds 购物车项ID列表
     * @return 批量转收藏结果
     */
    CollectBatchResultVO batchFromCart(Long userId, List<Long> cartIds);

    /**
     * 检查商品是否已被用户收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    boolean isCollected(Long userId, Long productId);

    /**
     * 批量检查商品是否已被用户收藏
     *
     * @param userId     用户ID
     * @param productIds 商品ID列表
     * @return 已收藏的商品ID列表
     */
    List<Long> batchIsCollected(Long userId, List<Long> productIds);

}