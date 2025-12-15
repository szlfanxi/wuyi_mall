package com.wuyi.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyi.mall.entity.Order;
import com.wuyi.mall.vo.OrderBatchResultVO;

import java.util.List;

/**
 * 订单服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface OrderService extends IService<Order> {

    /**
     * 从购物车批量下单
     *
     * @param userId  用户ID
     * @param cartIds 购物车项ID列表
     * @return 批量下单结果
     */
    OrderBatchResultVO batchFromCart(Long userId, List<Long> cartIds);

}