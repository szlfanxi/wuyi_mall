package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyi.mall.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 根据商品ID查询商品信息
     *
     * @param id 商品ID
     * @return 商品信息
     */
    Product selectById(@Param("id") Long id);

    /**
     * 根据商品ID查询商品库存
     *
     * @param id 商品ID
     * @return 商品库存
     */
    Integer selectStockById(@Param("id") Long id);

    /**
     * 根据商品ID列表查询商品信息
     *
     * @param ids 商品ID列表
     * @return 商品信息列表
     */
    List<Product> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 扣减商品库存（乐观锁）
     *
     * @param id       商品ID
     * @param quantity 扣减数量
     * @param version  版本号
     * @return 更新数量
     */
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity, @Param("version") Integer version);

    /**
     * 恢复商品库存（乐观锁）
     *
     * @param id       商品ID
     * @param quantity 恢复数量
     * @param version  版本号
     * @return 更新数量
     */
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity, @Param("version") Integer version);

    /**
     * 更新商品评分和评价数量
     *
     * @param id              商品ID
     * @param averageScore    平均评分
     * @param commentCount    评价数量
     * @return 更新数量
     */
    int updateCommentStats(@Param("id") Long productId, @Param("averageScore") BigDecimal averageScore, @Param("commentCount") Integer commentCount);

    /**
     * 更新商品价格
     *
     * @param productId 商品ID
     * @param newPrice  新价格
     * @return 更新数量
     */
    int updatePrice(@Param("id") Long productId, @Param("newPrice") BigDecimal newPrice);

    /**
     * 检查商品是否属于指定商家
     *
     * @param productId 商品ID
     * @param shopId    商家ID
     * @return 1表示属于，0表示不属于
     */
    int checkProductBelongsToShop(@Param("productId") Long productId, @Param("shopId") Long shopId);

    /**
     * 查询商品的当前价格
     *
     * @param productId 商品ID
     * @return 当前价格
     */
    BigDecimal selectCurrentPriceById(@Param("id") Long productId);

    /**
     * 查询商品的所属店铺ID
     *
     * @param productId 商品ID
     * @return 店铺ID
     */
    Long selectShopIdByProductId(@Param("id") Long productId);

}