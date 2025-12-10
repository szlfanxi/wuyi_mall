package com.wuyi.mall.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单编号生成工具类
 *
 * @author wuyi
 * @date 2025-12-04
 */
public class OrderNoUtil {

    /**
     * 日期格式化器，格式为YYYYMMDD
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 随机数生成器
     */
    private static final Random RANDOM = new Random();

    /**
     * 生成订单编号
     * 格式：YYYYMMDD+随机6位数字+user_id后4位
     *
     * @param userId 用户ID
     * @return 订单编号
     */
    public static String generateOrderNo(Long userId) {
        // 获取当前日期，格式为YYYYMMDD
        String dateStr = LocalDate.now().format(DATE_FORMATTER);

        // 生成随机6位数字
        String randomStr = String.format("%06d", RANDOM.nextInt(1000000));

        // 获取用户ID后4位，不足4位补0
        String userIdStr = String.format("%04d", userId % 10000);

        // 拼接订单编号
        return dateStr + randomStr + userIdStr;
    }

}