package com.wuyi.mall.enums;

import lombok.Getter;

/**
 * 订单状态枚举
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Getter
public enum OrderStatusEnum {

    /**
     * 取消状态
     */
    CANCEL(0, "取消"),
    /**
     * 客户下单
     */
    PLACE(1, "客户下单"),
    /**
     * 商家确认
     */
    CONFIRM(2, "商家确认"),
    /**
     * 备货完成
     */
    PREPARE(3, "备货完成"),
    /**
     * 已发货
     */
    DELIVER(4, "已发货"),
    /**
     * 已完成
     */
    COMPLETE(5, "已完成");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 构造方法
     *
     * @param code 状态码
     * @param name 状态名称
     */
    OrderStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据状态码获取状态枚举
     *
     * @param code 状态码
     * @return 状态枚举
     */
    public static OrderStatusEnum getByCode(Integer code) {
        for (OrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}