package com.wuyi.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyi.mall.entity.Product;

/**
 * 商品服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface ProductService extends IService<Product> {

    /**
     * 根据商品ID查询商品信息
     *
     * @param id 商品ID
     * @return 商品信息
     */
    Product getProductById(Long id);

    /**
     * 检查商品是否存在且状态正常
     *
     * @param id 商品ID
     * @return 商品信息
     */
    Product checkProductStatus(Long id);

    /**
     * 检查商品库存是否足够
     *
     * @param id       商品ID
     * @param quantity 购买数量
     */
    void checkProductStock(Long id, Integer quantity);

}