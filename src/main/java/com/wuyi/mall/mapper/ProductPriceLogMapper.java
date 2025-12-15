package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.ProductPriceLog;
import com.wuyi.mall.vo.ProductPriceLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 商品价格变动日志Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface ProductPriceLogMapper extends BaseMapper<ProductPriceLog> {

    /**
     * 查询商家商品价格变动日志（分页）
     *
     * @param page 分页参数
     * @param params 查询参数，包含productId、startTime、endTime
     * @return 价格变动日志列表
     */
    Page<ProductPriceLogVO> selectMerchantPriceLogs(Page<ProductPriceLogVO> page, @Param("params") Map<String, Object> params);

    /**
     * 查询管理员价格变动日志（分页，支持多条件筛选）
     *
     * @param page 分页参数
     * @param params 查询参数，包含productId、shopId、operateUserId、startTime、endTime
     * @return 价格变动日志列表
     */
    Page<ProductPriceLogVO> selectAdminPriceLogs(Page<ProductPriceLogVO> page, @Param("params") Map<String, Object> params);

}
