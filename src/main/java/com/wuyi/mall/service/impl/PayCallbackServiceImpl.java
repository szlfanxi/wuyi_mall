package com.wuyi.mall.service.impl;

import com.alibaba.fastjson.JSON;
import com.wuyi.mall.config.AlipayConfig;
import com.wuyi.mall.config.CreditCardPayConfig;
import com.wuyi.mall.config.WechatPayConfig;
import com.wuyi.mall.entity.Order;
import com.wuyi.mall.entity.OrderItem;
import com.wuyi.mall.entity.PaymentConfig;
import com.wuyi.mall.entity.PaymentRecord;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.OrderItemMapper;
import com.wuyi.mall.mapper.OrderMapper;
import com.wuyi.mall.mapper.PaymentConfigMapper;
import com.wuyi.mall.mapper.PaymentRecordMapper;
import com.wuyi.mall.service.PayCallbackService;
import com.wuyi.mall.util.PaySignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付回调服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class PayCallbackServiceImpl implements PayCallbackService {

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private PaymentConfigMapper paymentConfigMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private WechatPayConfig wechatPayConfig;

    @Autowired
    private CreditCardPayConfig creditCardPayConfig;

    /**
     * 支付方式：支付宝
     */
    private static final String PAY_TYPE_ALIPAY = "ALIPAY";

    /**
     * 支付方式：微信
     */
    private static final String PAY_TYPE_WECHAT = "WECHAT";

    /**
     * 支付方式：信用卡
     */
    private static final String PAY_TYPE_CREDIT_CARD = "CREDIT_CARD";

    /**
     * 支付状态：未支付
     */
    private static final Integer PAY_STATUS_UNPAID = 0;

    /**
     * 支付状态：支付成功
     */
    private static final Integer PAY_STATUS_SUCCESS = 1;

    /**
     * 支付状态：支付失败
     */
    private static final Integer PAY_STATUS_FAILED = 2;

    /**
     * 订单支付状态：未支付
     */
    private static final Integer ORDER_PAY_STATUS_UNPAID = 0;

    /**
     * 订单支付状态：已支付
     */
    private static final Integer ORDER_PAY_STATUS_PAID = 1;

    /**
     * 订单状态：待发货
     */
    private static final Integer ORDER_STATUS_TO_BE_SHIPPED = 2;

    /**
     * 处理支付宝支付回调
     *
     * @param params 回调参数
     * @return 回调处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> handleAlipayCallback(Map<String, Object> params) {
        return handleCallback(PAY_TYPE_ALIPAY, params);
    }

    /**
     * 处理微信支付回调
     *
     * @param params 回调参数
     * @return 回调处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> handleWechatCallback(Map<String, Object> params) {
        return handleCallback(PAY_TYPE_WECHAT, params);
    }

    /**
     * 处理信用卡支付回调
     *
     * @param params 回调参数
     * @return 回调处理结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> handleCreditCardCallback(Map<String, Object> params) {
        return handleCallback(PAY_TYPE_CREDIT_CARD, params);
    }

    /**
     * 统一处理支付回调
     *
     * @param payType 支付方式
     * @param params  回调参数
     * @return 回调处理结果
     */
    private Map<String, Object> handleCallback(String payType, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 验签
            if (!verifySign(payType, params)) {
                result.put("success", false);
                result.put("message", "验签失败");
                return result;
            }

            // 2. 解析回调参数
            String tradeNo = getTradeNoFromCallback(payType, params);
            String orderNo = getOrderNoFromCallback(payType, params);
            BigDecimal amount = getAmountFromCallback(payType, params);
            boolean isSuccess = isPaySuccessFromCallback(payType, params);

            // 3. 根据订单号查询订单
            Order order = orderMapper.selectByOrderNo(orderNo);
            if (order == null) {
                result.put("success", false);
                result.put("message", "订单不存在");
                return result;
            }

            // 4. 查询支付记录
            PaymentRecord paymentRecord = paymentRecordMapper.selectByOrderId(order.getId());
            if (paymentRecord == null) {
                result.put("success", false);
                result.put("message", "支付记录不存在");
                return result;
            }

            // 5. 幂等性校验
            if (paymentRecord.getStatus() != PAY_STATUS_UNPAID) {
                // 已处理过的支付，直接返回成功
                result.put("success", true);
                result.put("message", "已处理");
                return result;
            }

            // 6. 校验支付金额
            if (paymentRecord.getAmount().compareTo(amount) != 0) {
                result.put("success", false);
                result.put("message", "支付金额不一致");
                // 更新支付记录为失败状态
                updatePaymentRecordFailed(paymentRecord, tradeNo, params, "支付金额不一致");
                return result;
            }

            // 7. 更新支付记录和订单状态
            if (isSuccess) {
                // 支付成功
                updatePaymentRecordSuccess(paymentRecord, tradeNo, params);
                updateOrderToPaid(order);
            } else {
                // 支付失败
                updatePaymentRecordFailed(paymentRecord, tradeNo, params, "支付失败");
            }

            result.put("success", true);
            result.put("message", "处理成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "处理失败：" + e.getMessage());
        }

        return result;
    }

    /**
     * 更新支付记录为成功状态
     *
     * @param paymentRecord 支付记录
     * @param tradeNo       交易流水号
     * @param params        回调参数
     */
    private void updatePaymentRecordSuccess(PaymentRecord paymentRecord, String tradeNo, Map<String, Object> params) {
        paymentRecord.setStatus(PAY_STATUS_SUCCESS);
        paymentRecord.setPayTime(LocalDateTime.now());
        paymentRecord.setTradeNo(tradeNo);
        paymentRecord.setCallbackParams(JSON.toJSONString(params));
        paymentRecord.setCallbackTime(LocalDateTime.now());
        paymentRecordMapper.updateById(paymentRecord);
    }

    /**
     * 更新支付记录为失败状态
     *
     * @param paymentRecord 支付记录
     * @param tradeNo       交易流水号
     * @param params        回调参数
     * @param remark        失败原因
     */
    private void updatePaymentRecordFailed(PaymentRecord paymentRecord, String tradeNo, Map<String, Object> params, String remark) {
        paymentRecord.setStatus(PAY_STATUS_FAILED);
        paymentRecord.setTradeNo(tradeNo);
        paymentRecord.setCallbackParams(JSON.toJSONString(params));
        paymentRecord.setCallbackTime(LocalDateTime.now());
        paymentRecord.setRemark(remark);
        paymentRecordMapper.updateById(paymentRecord);
    }

    /**
     * 更新订单为已支付状态
     *
     * @param order 订单信息
     */
    private void updateOrderToPaid(Order order) {
        order.setPayStatus(ORDER_PAY_STATUS_PAID);
        order.setPayTime(LocalDateTime.now());
        order.setStatus(ORDER_STATUS_TO_BE_SHIPPED);
        orderMapper.updateById(order);
    }

    /**
     * 验证支付宝回调签名
     *
     * @param params 回调参数
     * @return 验签结果
     */
    @Override
    public boolean verifyAlipaySign(Map<String, Object> params) {
        // 使用支付宝公钥验签
        return PaySignUtil.verifyRsaSign(params, alipayConfig.getPublicKey(), params.get("sign").toString());
    }

    /**
     * 验证微信回调签名
     *
     * @param params 回调参数
     * @return 验签结果
     */
    @Override
    public boolean verifyWechatSign(Map<String, Object> params) {
        // 使用微信公钥验签
        return PaySignUtil.verifyRsaSign(params, wechatPayConfig.getPublicKey(), params.get("sign").toString());
    }

    /**
     * 验证信用卡支付回调签名
     *
     * @param params 回调参数
     * @return 验签结果
     */
    @Override
    public boolean verifyCreditCardSign(Map<String, Object> params) {
        // 使用信用卡支付网关公钥验签
        return PaySignUtil.verifyRsaSign(params, creditCardPayConfig.getPublicKey(), params.get("sign").toString());
    }

    /**
     * 统一验签方法
     *
     * @param payType 支付方式
     * @param params  回调参数
     * @return 验签结果
     */
    private boolean verifySign(String payType, Map<String, Object> params) {
        switch (payType) {
            case PAY_TYPE_ALIPAY:
                return verifyAlipaySign(params);
            case PAY_TYPE_WECHAT:
                return verifyWechatSign(params);
            case PAY_TYPE_CREDIT_CARD:
                return verifyCreditCardSign(params);
            default:
                return false;
        }
    }

    /**
     * 从回调参数中获取交易流水号
     *
     * @param payType 支付方式
     * @param params  回调参数
     * @return 交易流水号
     */
    private String getTradeNoFromCallback(String payType, Map<String, Object> params) {
        switch (payType) {
            case PAY_TYPE_ALIPAY:
                return params.get("trade_no").toString();
            case PAY_TYPE_WECHAT:
                return params.get("transaction_id").toString();
            case PAY_TYPE_CREDIT_CARD:
                return params.get("transaction_id").toString();
            default:
                throw new GlobalExceptionHandler.BusinessException(400, "不支持的支付方式");
        }
    }

    /**
     * 从回调参数中获取订单号
     *
     * @param payType 支付方式
     * @param params  回调参数
     * @return 订单号
     */
    private String getOrderNoFromCallback(String payType, Map<String, Object> params) {
        switch (payType) {
            case PAY_TYPE_ALIPAY:
                return params.get("out_trade_no").toString();
            case PAY_TYPE_WECHAT:
                return params.get("out_trade_no").toString();
            case PAY_TYPE_CREDIT_CARD:
                return params.get("order_no").toString();
            default:
                throw new GlobalExceptionHandler.BusinessException(400, "不支持的支付方式");
        }
    }

    /**
     * 从回调参数中获取支付金额
     *
     * @param payType 支付方式
     * @param params  回调参数
     * @return 支付金额
     */
    private BigDecimal getAmountFromCallback(String payType, Map<String, Object> params) {
        switch (payType) {
            case PAY_TYPE_ALIPAY:
                return new BigDecimal(params.get("total_amount").toString());
            case PAY_TYPE_WECHAT:
                return new BigDecimal(params.get("total_fee").toString()).divide(new BigDecimal("100"));
            case PAY_TYPE_CREDIT_CARD:
                return new BigDecimal(params.get("amount").toString());
            default:
                throw new GlobalExceptionHandler.BusinessException(400, "不支持的支付方式");
        }
    }

    /**
     * 从回调参数中判断支付是否成功
     *
     * @param payType 支付方式
     * @param params  回调参数
     * @return 支付是否成功
     */
    private boolean isPaySuccessFromCallback(String payType, Map<String, Object> params) {
        switch (payType) {
            case PAY_TYPE_ALIPAY:
                return "TRADE_SUCCESS".equals(params.get("trade_status"));
            case PAY_TYPE_WECHAT:
                return "SUCCESS".equals(params.get("result_code")) && "SUCCESS".equals(params.get("return_code"));
            case PAY_TYPE_CREDIT_CARD:
                return "SUCCESS".equals(params.get("status"));
            default:
                throw new GlobalExceptionHandler.BusinessException(400, "不支持的支付方式");
        }
    }
}
