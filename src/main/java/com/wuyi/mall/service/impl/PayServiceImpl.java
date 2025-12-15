package com.wuyi.mall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.Order;
import com.wuyi.mall.entity.OrderItem;
import com.wuyi.mall.entity.PaymentConfig;
import com.wuyi.mall.entity.PaymentRecord;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.OrderItemMapper;
import com.wuyi.mall.mapper.OrderMapper;
import com.wuyi.mall.mapper.PaymentConfigMapper;
import com.wuyi.mall.mapper.PaymentRecordMapper;
import com.wuyi.mall.service.MarketingService;
import com.wuyi.mall.service.OrderService;
import com.wuyi.mall.service.PayCallbackService;
import com.wuyi.mall.service.PayService;
import com.wuyi.mall.vo.PayRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.alibaba.fastjson.JSON;
import java.util.*;

/**
 * 支付服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class PayServiceImpl implements PayService {

    /**
     * 支付记录Mapper
     */
    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    /**
     * 支付配置Mapper
     */
    @Autowired
    private PaymentConfigMapper paymentConfigMapper;

    /**
     * 订单Mapper
     */
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 订单详情Mapper
     */
    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 订单服务
     */
    @Autowired
    private OrderService orderService;

    /**
     * 营销服务
     */
    @Autowired
    private MarketingService marketingService;

    @Autowired
    private PayCallbackService payCallbackService;

    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
     * 支付状态：支付超时
     */
    private static final Integer PAY_STATUS_TIMEOUT = 3;

    /**
     * 订单状态：客户下单
     */
    private static final Integer ORDER_STATUS_PLACED = 1;

    /**
     * 订单状态：取消
     */
    private static final Integer ORDER_STATUS_CANCELED = 0;

    /**
     * 订单支付状态：未支付
     */
    private static final Integer ORDER_PAY_STATUS_UNPAID = 0;

    /**
     * 订单支付状态：已支付
     */
    private static final Integer ORDER_PAY_STATUS_PAID = 1;

    /**
     * 订单支付状态：支付超时
     */
    private static final Integer ORDER_PAY_STATUS_TIMEOUT = 2;

    /**
     * 支付超时时间：15分钟
     */
    private static final int PAY_TIMEOUT_MINUTES = 15;

    /**
     * 发起支付
     *
     * @param userId      用户ID
     * @param payRequestVO 支付请求参数
     * @return 支付结果，包含支付参数和支付记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> initiatePay(Long userId, PayRequestVO payRequestVO) {
        Long orderId = payRequestVO.getOrderId();
        String payType = payRequestVO.getPayType();
        String clientIp = payRequestVO.getClientIp();

        // 1. 查询订单信息
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单不存在");
        }

        // 2. 校验订单归属
        if (!order.getUserId().equals(userId)) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单不属于当前用户");
        }

        // 3. 校验订单状态和支付状态
        if (order.getStatus() != ORDER_STATUS_PLACED || order.getPayStatus() != ORDER_PAY_STATUS_UNPAID) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单已支付或状态异常");
        }

        // 4. 校验订单是否超时
        LocalDateTime now = LocalDateTime.now();
        if (order.getCreateTime().plusMinutes(PAY_TIMEOUT_MINUTES).isBefore(now)) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单已超时");
        }

        // 5. 查询支付配置
        PaymentConfig paymentConfig = paymentConfigMapper.selectByPayTypeAndStatus(payType, 1);
        if (paymentConfig == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "支付方式暂不支持");
        }

        // 6. 计算实付金额
        BigDecimal actualPayAmount = calculateActualPayAmount(orderId);

        // 7. 生成支付参数
        Map<String, Object> payParams = generatePayParams(payType, order, actualPayAmount, clientIp, paymentConfig);

        // 8. 创建支付记录
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setOrderId(orderId);
        paymentRecord.setOrderNo(order.getOrderNo());
        paymentRecord.setUserId(userId);
        paymentRecord.setPayType(payType);
        paymentRecord.setAmount(actualPayAmount);
        paymentRecord.setStatus(PAY_STATUS_UNPAID);
        paymentRecord.setClientIp(clientIp);
        paymentRecord.setCreateTime(now);
        paymentRecord.setPayParams(JSON.toJSONString(payParams));
        paymentRecordMapper.insert(paymentRecord);

        // 9. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("payParams", payParams);
        result.put("paymentId", paymentRecord.getId());

        return result;
    }

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
    @Override
    public Page<PaymentRecord> getUserPayRecords(Long userId, Long orderId, String payType, String startTime, String endTime, Integer pageNum, Integer pageSize) {
        Page<PaymentRecord> page = new Page<>(pageNum, pageSize);
        LocalDateTime startDateTime = startTime != null ? LocalDateTime.parse(startTime, DATE_TIME_FORMATTER) : null;
        LocalDateTime endDateTime = endTime != null ? LocalDateTime.parse(endTime, DATE_TIME_FORMATTER) : null;
        return paymentRecordMapper.selectByUserId(page, userId, orderId, payType, startDateTime, endDateTime);
    }

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
    @Override
    public Page<PaymentRecord> getMerchantPayRecords(Long shopId, String orderNo, Integer payStatus, String startTime, String endTime, Integer pageNum, Integer pageSize) {
        Page<PaymentRecord> page = new Page<>(pageNum, pageSize);
        LocalDateTime startDateTime = startTime != null ? LocalDateTime.parse(startTime, DATE_TIME_FORMATTER) : null;
        LocalDateTime endDateTime = endTime != null ? LocalDateTime.parse(endTime, DATE_TIME_FORMATTER) : null;
        return paymentRecordMapper.selectByShopId(page, shopId, orderNo, payStatus, startDateTime, endDateTime);
    }

    /**
     * 管理员查询全平台支付记录
     *
     * @param params   查询参数，包含userId、shopId、payType、payStatus、时间范围等
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 支付记录列表（分页）
     */
    @Override
    public Page<PaymentRecord> getAdminPayRecords(Map<String, Object> params, Integer pageNum, Integer pageSize) {
        Page<PaymentRecord> page = new Page<>(pageNum, pageSize);
        return paymentRecordMapper.selectByAdmin(page, params);
    }

    /**
     * 处理支付回调
     *
     * @param payType 支付方式
     * @param params  回调参数
     * @return 回调处理结果
     */
    @Override
    public Map<String, Object> handleCallback(String payType, Map<String, Object> params) {
        // 根据支付方式调用不同的回调处理方法
        switch (payType) {
            case PAY_TYPE_ALIPAY:
                return payCallbackService.handleAlipayCallback(params);
            case PAY_TYPE_WECHAT:
                return payCallbackService.handleWechatCallback(params);
            case PAY_TYPE_CREDIT_CARD:
                return payCallbackService.handleCreditCardCallback(params);
            default:
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "不支持的支付方式");
                return result;
        }
    }

    /**
     * 处理支付超时订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleTimeoutPayments() {
        // 1. 查询超时未支付的支付记录
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(PAY_TIMEOUT_MINUTES);
        List<PaymentRecord> timeoutPayments = paymentRecordMapper.selectTimeoutPayments(timeoutTime);

        for (PaymentRecord paymentRecord : timeoutPayments) {
            // 2. 查询订单
            Order order = orderMapper.selectById(paymentRecord.getOrderId());
            if (order == null) {
                continue;
            }

            // 3. 更新订单状态为取消
            order.setStatus(ORDER_STATUS_CANCELED);
            order.setPayStatus(ORDER_PAY_STATUS_TIMEOUT);
            orderMapper.updateById(order);

            // 4. 恢复商品库存
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
            for (OrderItem orderItem : orderItems) {
                // 恢复库存（这里需要调用商品服务的恢复库存方法，使用乐观锁）
                // 简化处理：直接更新商品库存
                // productMapper.increaseStock(orderItem.getProductId(), orderItem.getQuantity(), orderItem.getVersion());
            }

            // 5. 更新支付记录状态为超时
            paymentRecord.setStatus(PAY_STATUS_TIMEOUT);
            paymentRecord.setRemark("支付超时自动取消");
            paymentRecordMapper.updateById(paymentRecord);
        }
    }

    /**
     * 根据订单ID查询支付记录
     *
     * @param orderId 订单ID
     * @return 支付记录
     */
    @Override
    public PaymentRecord getPaymentByOrderId(Long orderId) {
        return paymentRecordMapper.selectByOrderId(orderId);
    }

    /**
     * 根据交易流水号查询支付记录
     *
     * @param tradeNo 交易流水号
     * @return 支付记录
     */
    @Override
    public PaymentRecord getPaymentByTradeNo(String tradeNo) {
        return paymentRecordMapper.selectByTradeNo(tradeNo);
    }

    /**
     * 计算订单实付金额（订单金额-优惠券优惠-打折优惠）
     *
     * @param orderId 订单ID
     * @return 实付金额
     */
    @Override
    public BigDecimal calculateActualPayAmount(Long orderId) {
        // 查询订单信息
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单不存在");
        }

        // 查询订单商品列表
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        if (orderItems == null || orderItems.isEmpty()) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单商品不存在");
        }

        // 调用PayAmountUtil计算实付金额
        return com.wuyi.mall.util.PayAmountUtil.calculateActualPayAmount(order, orderItems, null);
    }

    /**
     * 生成支付参数
     *
     * @param payType        支付方式
     * @param order          订单信息
     * @param actualPayAmount 实付金额
     * @param clientIp       客户端IP
     * @param paymentConfig  支付配置
     * @return 支付参数
     */
    private Map<String, Object> generatePayParams(String payType, Order order, BigDecimal actualPayAmount, String clientIp, PaymentConfig paymentConfig) {
        Map<String, Object> payParams = new HashMap<>();

        switch (payType) {
            case PAY_TYPE_ALIPAY:
                // 支付宝支付参数生成
                payParams.put("outTradeNo", order.getOrderNo());
                payParams.put("totalAmount", actualPayAmount);
                payParams.put("subject", "无艺商城订单支付");
                payParams.put("body", "订单号：" + order.getOrderNo());
                payParams.put("productCode", "FAST_INSTANT_TRADE_PAY");
                payParams.put("qrCode", "https://qr.alipay.com/xxx"); // 模拟二维码
                break;
            case PAY_TYPE_WECHAT:
                // 微信支付参数生成
                payParams.put("appId", paymentConfig.getAppId());
                payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                payParams.put("nonceStr", UUID.randomUUID().toString());
                payParams.put("package", "prepay_id=wx1234567890"); // 模拟prepay_id
                payParams.put("signType", "RSA");
                payParams.put("paySign", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"); // 模拟签名
                break;
            case PAY_TYPE_CREDIT_CARD:
                // 信用卡支付参数生成
                payParams.put("merchantId", paymentConfig.getMerchantId());
                payParams.put("orderNo", order.getOrderNo());
                payParams.put("amount", actualPayAmount);
                payParams.put("redirectUrl", paymentConfig.getReturnUrl());
                break;
            default:
                throw new GlobalExceptionHandler.BusinessException(400, "不支持的支付方式");
        }

        return payParams;
    }


}
