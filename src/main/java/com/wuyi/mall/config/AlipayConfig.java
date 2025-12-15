package com.wuyi.mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝配置类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "payment.alipay")
public class AlipayConfig {

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 支付网关URL
     */
    private String gatewayUrl;

    /**
     * 同步回调URL
     */
    private String returnUrl;

    /**
     * 异步通知URL
     */
    private String notifyUrl;
}
