package com.wuyi.mall.vo;

import lombok.Data;

/**
 * 订单操作结果VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class OrderOperateResultVO {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 当前状态
     */
    private Integer currentStatus;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 失败原因
     */
    private String failReason;

}