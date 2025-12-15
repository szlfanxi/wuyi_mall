package com.wuyi.mall.enums;

import lombok.Getter;

/**
 * 订单操作类型枚举
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Getter
public enum OrderOperateTypeEnum {

    /**
     * 确认订单
     */
    CONFIRM("CONFIRM", "确认订单"),
    /**
     * 备货
     */
    PREPARE("PREPARE", "备货"),
    /**
     * 发货
     */
    DELIVER("DELIVER", "发货"),
    /**
     * 客户取消
     */
    CANCEL_BY_CUSTOMER("CANCEL_BY_CUSTOMER", "客户取消"),
    /**
     * 商家取消
     */
    CANCEL_BY_MERCHANT("CANCEL_BY_MERCHANT", "商家取消");

    /**
     * 操作类型码
     */
    private final String code;

    /**
     * 操作类型名称
     */
    private final String name;

    /**
     * 构造方法
     *
     * @param code 操作类型码
     * @param name 操作类型名称
     */
    OrderOperateTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据操作类型码获取操作类型枚举
     *
     * @param code 操作类型码
     * @return 操作类型枚举
     */
    public static OrderOperateTypeEnum getByCode(String code) {
        for (OrderOperateTypeEnum operateType : values()) {
            if (operateType.getCode().equals(code)) {
                return operateType;
            }
        }
        return null;
    }

}