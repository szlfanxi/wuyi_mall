package com.wuyi.mall.task;

import com.wuyi.mall.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 支付超时订单处理定时任务
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Component
public class PayTimeoutTask {

    /**
     * 支付服务
     */
    @Autowired
    private PayService payService;

    /**
     * 每5分钟执行一次，处理超时未支付订单
     * 表达式含义：秒 分 时 日 月 周
     * 0 0/5 * * * ? 表示每5分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void handleTimeoutPayments() {
        payService.handleTimeoutPayments();
    }

}