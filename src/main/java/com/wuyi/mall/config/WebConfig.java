package com.wuyi.mall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类，用于配置跨域等
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置跨域
     *
     * @param registry CorsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有路径跨域
        registry.addMapping("/**")
                // 允许所有来源
                .allowedOrigins("*")
                // 允许所有方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许所有头部
                .allowedHeaders("*")
                // 允许携带凭证
                .allowCredentials(true)
                // 预检请求有效期，单位：秒
                .maxAge(3600);
    }

}