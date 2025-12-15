package com.wuyi.mall.vo;

import lombok.Data;

import java.util.List;

/**
 * 批量下单结果VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class OrderBatchResultVO {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败项列表
     */
    private List<FailItem> failItems;

    /**
     * 失败项内部类
     */
    @Data
    public static class FailItem {
        /**
         * 购物车项ID
         */
        private Long cartId;

        /**
         * 失败原因
         */
        private String reason;
    }

}