package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyi.mall.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 更新订单状态
     *
     * @param id         订单ID
     * @param status     新状态
     * @param beforeStatus 旧状态（用于乐观锁）
     * @return 更新数量
     */
    int updateStatusById(@Param("id") Long id, @Param("status") Integer status, @Param("beforeStatus") Integer beforeStatus);

    /**
     * 检查订单是否归属当前商家
     *
     * @param orderId 订单ID
     * @param userId  商家ID
     * @return 订单数量，1表示归属，0表示不归属
     */
    int checkOrderBelongToMerchant(@Param("orderId") Long orderId, @Param("userId") Long userId);

    /**
     * 根据订单ID查询订单（带行锁）
     *
     * @param id 订单ID
     * @return 订单信息
     */
    Order selectOrderByIdWithLock(@Param("id") Long id);

    /**
     * 根据订单编号查询订单
     *
     * @param orderNo 订单编号
     * @return 订单信息
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);

}