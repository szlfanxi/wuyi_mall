package com.wuyi.mall.service;

import java.util.Map;

/**
 * 支付回调服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface PayCallbackService {

    /**
     * 处理支付宝支付回调
     *
     * @param params 回调参数
     * @return 回调处理结果，包含success字段（true/false）和message字段
     */
    Map<String, Object> handleAlipayCallback(Map<String, Object> params);

    /**
     * 处理微信支付回调
     *
     * @param params 回调参数
     * @return 回调处理结果，包含success字段（true/false）和message字段
     */
    Map<String, Object> handleWechatCallback(Map<String, Object> params);

    /**
     * 处理信用卡支付回调
     *
     * @param params 回调参数
     * @return 回调处理结果，包含success字段（true/false）和message字段
     */
    Map<String, Object> handleCreditCardCallback(Map<String, Object> params);

    /**
     * 验证支付宝回调签名
     *
     * @param params 回调参数
     * @return 验签结果
     */
    boolean verifyAlipaySign(Map<String, Object> params);

    /**
     * 验证微信回调签名
     *
     * @param params 回调参数
     * @return 验签结果
     */
    boolean verifyWechatSign(Map<String, Object> params);

    /**
     * 验证信用卡支付回调签名
     *
     * @param params 回调参数
     * @return 验签结果
     */
    boolean verifyCreditCardSign(Map<String, Object> params);
}
