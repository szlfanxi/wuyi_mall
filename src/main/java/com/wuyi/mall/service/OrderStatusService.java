package com.wuyi.mall.service;

import com.wuyi.mall.vo.OrderOperateResultVO;

/**
 * 订单状态服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface OrderStatusService {

    /**
     * 订单状态流转操作
     *
     * @param orderId     订单ID
     * @param operateType 操作类型
     * @param userId      操作人ID
     * @return 操作结果
     */
    OrderOperateResultVO operateOrder(Long orderId, String operateType, Long userId);

    /**
     * 客户取消订单
     *
     * @param orderId 订单ID
     * @param userId  客户ID
     * @return 操作结果
     */
    OrderOperateResultVO cancelOrderByCustomer(Long orderId, Long userId);

    /**
     * 商家取消订单
     *
     * @param orderId 订单ID
     * @param userId  商家ID
     * @return 操作结果
     */
    OrderOperateResultVO cancelOrderByMerchant(Long orderId, Long userId);

    /**
     * 检查订单状态是否允许操作
     *
     * @param currentStatus 当前状态
     * @param operateType   操作类型
     * @return 允许返回true，不允许返回false
     */
    boolean checkOrderStatusCanOperate(Integer currentStatus, String operateType);

}