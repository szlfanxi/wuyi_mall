package com.wuyi.mall.controller;

import com.wuyi.mall.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付回调控制器
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/pay")
public class PayCallbackController {

    /**
     * 支付服务
     */
    @Autowired
    private PayService payService;

    /**
     * 支付回调处理
     *
     * @param payType 支付方式
     * @param params  回调参数
     * @return 回调处理结果
     */
    @PostMapping("/callback/{payType}")
    public Object handleCallback(@PathVariable String payType, @RequestParam Map<String, Object> params, @RequestBody(required = false) Map<String, Object> bodyParams) {
        // 根据支付方式选择不同的回调参数处理方式
        Map<String, Object> callbackParams;
        if ("WECHAT".equals(payType)) {
            // 微信支付回调可能使用XML格式，这里简化处理
            callbackParams = bodyParams != null ? bodyParams : params;
        } else {
            // 支付宝和信用卡支付使用表单参数
            callbackParams = params;
        }

        // 调用支付服务处理回调
        Map<String, Object> result = payService.handleCallback(payType, callbackParams);

        // 根据支付方式返回不同的响应格式
        if ("ALIPAY".equals(payType)) {
            // 支付宝要求返回success或failure字符串
            return result.get("success") == Boolean.TRUE ? "success" : "failure";
        } else if ("WECHAT".equals(payType)) {
            // 微信要求返回XML格式的SUCCESS或FAIL
            return result.get("success") == Boolean.TRUE ? "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>" : "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        } else {
            // 信用卡支付返回JSON格式
            return result;
        }
    }

}