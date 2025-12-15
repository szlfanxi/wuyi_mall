package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyi.mall.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 根据用户ID和商品ID查询购物车项
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 购物车项
     */
    Cart selectByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 根据用户ID查询购物车列表
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<Cart> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据购物车项ID列表查询购物车项
     *
     * @param ids 购物车项ID列表
     * @return 购物车项列表
     */
    List<Cart> selectByIds(@Param("ids") List<Long> ids);

    /**
     * 根据用户ID和购物车项ID列表查询购物车项
     *
     * @param userId 用户ID
     * @param ids    购物车项ID列表
     * @return 购物车项列表
     */
    List<Cart> selectByUserIdAndIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    /**
     * 更新购物车项数量
     *
     * @param id       购物车项ID
     * @param quantity 购买数量
     * @return 更新结果
     */
    int updateQuantityById(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 批量删除购物车项
     *
     * @param ids 购物车项ID列表
     * @return 删除数量
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);

}