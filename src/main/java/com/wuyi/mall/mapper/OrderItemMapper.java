package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyi.mall.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单详情Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 批量插入订单详情
     *
     * @param orderItems 订单详情列表
     * @return 插入数量
     */
    int batchInsert(@Param("orderItems") List<OrderItem> orderItems);

    /**
     * 根据订单ID查询订单详情列表
     *
     * @param orderId 订单ID
     * @return 订单详情列表
     */
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);

}