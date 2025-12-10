package com.wuyi.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.PaymentRecord;
import com.wuyi.mall.vo.PayRequestVO;

import java.util.Map;

/**
 * 支付服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface PayService {

    /**
     * 发起支付
     *
     * @param userId      用户ID
     * @param payRequestVO 支付请求参数
     * @return 支付结果，包含支付参数和支付记录ID
     */
    Map<String, Object> initiatePay(Long userId, PayRequestVO payRequestVO);

    /**
     * 客户查询自身支付记录
     *
     * @param userId    用户ID
     * @param orderId   订单ID（可选）
     * @param payType   支付方式（可选）
     * @param startTime 开始时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime   结束时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     * @param pageNum   页码
     * @param pageSize  每页条数
     * @return 支付记录列表（分页）
     */
    Page<PaymentRecord> getUserPayRecords(Long userId, Long orderId, String payType, String startTime, String endTime, Integer pageNum, Integer pageSize);

    /**
     * 商家查询自家订单支付记录
     *
     * @param shopId     商铺ID
     * @param orderNo    订单编号（可选）
     * @param payStatus  支付状态（可选）
     * @param startTime  开始时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime    结束时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 支付记录列表（分页）
     */
    Page<PaymentRecord> getMerchantPayRecords(Long shopId, String orderNo, Integer payStatus, String startTime, String endTime, Integer pageNum, Integer pageSize);

    /**
     * 管理员查询全平台支付记录
     *
     * @param params   查询参数，包含userId、shopId、payType、payStatus、时间范围等
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 支付记录列表（分页）
     */
    Page<PaymentRecord> getAdminPayRecords(Map<String, Object> params, Integer pageNum, Integer pageSize);

    /**
     * 处理支付回调
     *
     * @param payType 支付方式
     * @param params  回调参数
     * @return 回调处理结果
     */
    Map<String, Object> handleCallback(String payType, Map<String, Object> params);

    /**
     * 处理支付超时订单
     */
    void handleTimeoutPayments();

    /**
     * 根据订单ID查询支付记录
     *
     * @param orderId 订单ID
     * @return 支付记录
     */
    PaymentRecord getPaymentByOrderId(Long orderId);

    /**
     * 根据交易流水号查询支付记录
     *
     * @param tradeNo 交易流水号
     * @return 支付记录
     */
    PaymentRecord getPaymentByTradeNo(String tradeNo);

    /**
     * 计算订单实付金额（订单金额-优惠券优惠-打折优惠）
     *
     * @param orderId 订单ID
     * @return 实付金额
     */
    java.math.BigDecimal calculateActualPayAmount(Long orderId);

}
