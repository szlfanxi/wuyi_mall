package com.wuyi.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyi.mall.entity.Product;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.ProductMapper;
import com.wuyi.mall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    /**
     * 商品Mapper
     */
    @Autowired
    private ProductMapper productMapper;

    /**
     * 根据商品ID查询商品信息
     *
     * @param id 商品ID
     * @return 商品信息
     */
    @Override
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }

    /**
     * 检查商品是否存在且状态正常
     *
     * @param id 商品ID
     * @return 商品信息
     */
    @Override
    public Product checkProductStatus(Long id) {
        // 查询商品信息
        Product product = productMapper.selectById(id);

        // 检查商品是否存在
        if (product == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "商品不存在");
        }

        // 检查商品是否已下架
        if (product.getStatus() != 1) {
            throw new GlobalExceptionHandler.BusinessException(400, "商品已下架");
        }

        // 检查商品是否已删除
        if (product.getDeleted() != 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "商品不存在");
        }

        return product;
    }

    /**
     * 检查商品库存是否足够
     *
     * @param id       商品ID
     * @param quantity 购买数量
     */
    @Override
    public void checkProductStock(Long id, Integer quantity) {
        // 查询商品信息
        Product product = productMapper.selectById(id);

        // 检查商品库存是否足够
        if (product.getStockNum() < quantity) {
            throw new GlobalExceptionHandler.BusinessException(400, "库存不足");
        }
    }

}