package com.wuyi.mall.vo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 修改商品价格请求VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class ProductPriceModifyVO {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 新价格
     */
    @NotNull(message = "新价格不能为空")
    @DecimalMin(value = "0.01", message = "新价格必须大于0")
    private BigDecimal newPrice;

}
