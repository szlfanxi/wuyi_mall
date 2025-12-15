package com.wuyi.mall.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品价格变动日志VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class ProductPriceLogVO {

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商铺ID
     */
    private Long shopId;

    /**
     * 商铺名称
     */
    private String shopName;

    /**
     * 旧价格
     */
    private BigDecimal oldPrice;

    /**
     * 新价格
     */
    private BigDecimal newPrice;

    /**
     * 操作人ID
     */
    private Long operateUserId;

    /**
     * 操作人昵称
     */
    private String operateUserNickname;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;

    /**
     * 操作类型
     */
    private String operateType;

}
