package com.wuyi.mall.vo;

import lombok.Data;

import java.util.List;

/**
 * 批量转收藏结果VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class CollectBatchResultVO {

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