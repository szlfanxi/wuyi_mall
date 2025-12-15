package com.wuyi.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 无艺商城启动类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@SpringBootApplication
@MapperScan("com.wuyi.mall.mapper")
@EnableScheduling
public class WuyiMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(WuyiMallApplication.class, args);
    }

}