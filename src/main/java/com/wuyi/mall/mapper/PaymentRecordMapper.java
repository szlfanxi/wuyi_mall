package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 支付记录Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    /**
     * 根据订单ID查询支付记录
     *
     * @param orderId 订单ID
     * @return 支付记录
     */
    PaymentRecord selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据交易流水号查询支付记录
     *
     * @param tradeNo 交易流水号
     * @return 支付记录
     */
    PaymentRecord selectByTradeNo(@Param("tradeNo") String tradeNo);

    /**
     * 客户查询自身支付记录（分页）
     *
     * @param page      分页参数
     * @param userId    用户ID
     * @param orderId   订单ID（可选）
     * @param payType   支付方式（可选）
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @return 支付记录列表
     */
    Page<PaymentRecord> selectByUserId(Page<PaymentRecord> page, @Param("userId") Long userId, @Param("orderId") Long orderId, @Param("payType") String payType, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 商家查询自家订单支付记录（分页）
     *
     * @param page     分页参数
     * @param shopId   商铺ID
     * @param orderNo  订单编号（可选）
     * @param payStatus 支付状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @return 支付记录列表
     */
    Page<PaymentRecord> selectByShopId(Page<PaymentRecord> page, @Param("shopId") Long shopId, @Param("orderNo") String orderNo, @Param("payStatus") Integer payStatus, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 管理员查询全平台支付记录（分页）
     *
     * @param page      分页参数
     * @param params    查询参数
     * @return 支付记录列表
     */
    Page<PaymentRecord> selectByAdmin(Page<PaymentRecord> page, @Param("params") Map<String, Object> params);

    /**
     * 查询超时未支付的支付记录
     *
     * @param timeoutTime 超时时间
     * @return 支付记录列表
     */
    java.util.List<PaymentRecord> selectTimeoutPayments(@Param("timeoutTime") LocalDateTime timeoutTime);

    /**
     * 更新支付状态
     *
     * @param id     支付记录ID
     * @param status 新状态
     * @param tradeNo 交易流水号
     * @return 更新数量
     */
    int updatePayStatus(@Param("id") Long id, @Param("status") Integer status, @Param("tradeNo") String tradeNo);
}
