package com.wuyi.mall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.Product;
import com.wuyi.mall.entity.ProductPriceLog;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.ProductMapper;
import com.wuyi.mall.mapper.ProductPriceLogMapper;
import com.wuyi.mall.service.ProductPriceService;
import com.wuyi.mall.vo.ProductPriceLogVO;
import com.wuyi.mall.vo.ProductPriceModifyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 商品价格管理服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class ProductPriceServiceImpl implements ProductPriceService {

    /**
     * 商品Mapper
     */
    @Autowired
    private ProductMapper productMapper;

    /**
     * 商品价格变动日志Mapper
     */
    @Autowired
    private ProductPriceLogMapper productPriceLogMapper;

    /**
     * 价格修改操作类型
     */
    private static final String OPERATE_TYPE_PRICE_MODIFY = "PRICE_MODIFY";

    /**
     * 修改商品价格
     *
     * @param shopId                商家关联的商铺ID
     * @param operateUserId         操作人ID
     * @param productPriceModifyVO  修改价格请求参数
     * @return 修改结果，包含商品ID、旧价格、新价格
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> modifyProductPrice(Long shopId, Long operateUserId, ProductPriceModifyVO productPriceModifyVO) {
        Long productId = productPriceModifyVO.getProductId();
        BigDecimal newPrice = productPriceModifyVO.getNewPrice();

        // 1. 检查商品是否可以修改价格
        checkProductPriceModifyAble(shopId, productId, newPrice);

        // 2. 查询商品当前价格
        BigDecimal oldPrice = productMapper.selectCurrentPriceById(productId);
        if (oldPrice == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "商品价格异常");
        }

        // 3. 更新商品价格
        int updateResult = productMapper.updatePrice(productId, newPrice);
        if (updateResult == 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "商品价格更新失败");
        }

        // 4. 新增价格变动日志
        ProductPriceLog priceLog = new ProductPriceLog();
        priceLog.setProductId(productId);
        priceLog.setOldPrice(oldPrice);
        priceLog.setNewPrice(newPrice);
        priceLog.setOperateUserId(operateUserId);
        priceLog.setOperateTime(LocalDateTime.now());
        priceLog.setOperateType(OPERATE_TYPE_PRICE_MODIFY);

        int insertResult = productPriceLogMapper.insert(priceLog);
        if (insertResult == 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "价格变动日志记录失败");
        }

        // 5. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("productId", productId);
        result.put("oldPrice", oldPrice);
        result.put("newPrice", newPrice);

        return result;
    }

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
    @Override
    public Page<ProductPriceLogVO> getMerchantPriceLogs(Long shopId, Long productId, Integer pageNum, Integer pageSize, String startTime, String endTime) {
        // 1. 检查商品是否属于当前商家
        int belongs = productMapper.checkProductBelongsToShop(productId, shopId);
        if (belongs == 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "商品不属于当前商家");
        }

        // 2. 设置分页参数
        Page<ProductPriceLogVO> page = new Page<>(pageNum, pageSize);

        // 3. 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        // 4. 查询日志列表
        return productPriceLogMapper.selectMerchantPriceLogs(page, params);
    }

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
    @Override
    public Page<ProductPriceLogVO> getAdminPriceLogs(Integer pageNum, Integer pageSize, Long productId, Long shopId, Long operateUserId, String startTime, String endTime) {
        // 1. 设置分页参数
        Page<ProductPriceLogVO> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("shopId", shopId);
        params.put("operateUserId", operateUserId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        // 3. 查询日志列表
        return productPriceLogMapper.selectAdminPriceLogs(page, params);
    }

    /**
     * 检查商品是否可以修改价格
     *
     * @param shopId     商家关联的商铺ID
     * @param productId  商品ID
     * @param newPrice   新价格
     * @return 可修改返回true，否则返回false
     */
    @Override
    public boolean checkProductPriceModifyAble(Long shopId, Long productId, BigDecimal newPrice) {
        // 1. 检查商品是否存在且未下架
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "商品不存在或已下架");
        }

        // 2. 检查商品是否属于当前商家
        int belongs = productMapper.checkProductBelongsToShop(productId, shopId);
        if (belongs == 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "商品不属于当前商家");
        }

        // 3. 检查新价格是否大于0
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "新价格必须大于0");
        }

        // 4. 检查新价格是否与当前价格一致
        BigDecimal currentPrice = product.getCurrentPrice();
        if (currentPrice != null && newPrice.compareTo(currentPrice) == 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "新价格与当前价格一致，无需修改");
        }

        return true;
    }

}