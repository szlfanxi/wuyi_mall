package com.wuyi.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.vo.ProductPriceLogVO;
import com.wuyi.mall.vo.ProductPriceModifyVO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 商品价格管理服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface ProductPriceService {

    /**
     * 修改商品价格
     *
     * @param shopId                商家关联的商铺ID
     * @param operateUserId         操作人ID
     * @param productPriceModifyVO  修改价格请求参数
     * @return 修改结果，包含商品ID、旧价格、新价格
     */
    Map<String, Object> modifyProductPrice(Long shopId, Long operateUserId, ProductPriceModifyVO productPriceModifyVO);

    /**
     * 查询商家商品价格变动日志
     *
     * @param shopId    商家关联的商铺ID
     * @param productId 商品ID
     * @param pageNum   页码
     * @param pageSize  每页条数
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @return 价格变动日志列表（分页）
     */
    Page<ProductPriceLogVO> getMerchantPriceLogs(Long shopId, Long productId, Integer pageNum, Integer pageSize, String startTime, String endTime);

    /**
     * 查询管理员价格变动日志（支持多条件筛选）
     *
     * @param pageNum        页码
     * @param pageSize       每页条数
     * @param productId      商品ID（可选）
     * @param shopId         商铺ID（可选）
     * @param operateUserId  操作人ID（可选）
     * @param startTime      开始时间（可选）
     * @param endTime        结束时间（可选）
     * @return 价格变动日志列表（分页）
     */
    Page<ProductPriceLogVO> getAdminPriceLogs(Integer pageNum, Integer pageSize, Long productId, Long shopId, Long operateUserId, String startTime, String endTime);

    /**
     * 检查商品是否可以修改价格
     *
     * @param shopId     商家关联的商铺ID
     * @param productId  商品ID
     * @param newPrice   新价格
     * @return 可修改返回true，否则返回false
     */
    boolean checkProductPriceModifyAble(Long shopId, Long productId, BigDecimal newPrice);

}