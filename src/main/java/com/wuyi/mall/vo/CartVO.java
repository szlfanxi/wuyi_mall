package com.wuyi.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车返回VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class CartVO {

    /**
     * 购物车项ID
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
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 商品库存
     */
    private Integer stock;

    /**
     * 商品状态，0-下架，1-上架
     */
    private Integer status;

    /**
     * 库存状态，true-库存充足，false-库存不足
     */
    private Boolean stockStatus;

    /**
     * 商品描述
     */
    private String description;

}