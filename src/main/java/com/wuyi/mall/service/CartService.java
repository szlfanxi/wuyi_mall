package com.wuyi.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyi.mall.entity.Cart;
import com.wuyi.mall.vo.CartVO;

import java.util.List;

/**
 * 购物车服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface CartService extends IService<Cart> {

    /**
     * 添加商品到购物车
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  购买数量
     * @return 购物车项ID
     */
    Long addToCart(Long userId, Long productId, Integer quantity);

    /**
     * 查询用户购物车列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 购物车列表
     */
    List<CartVO> getCartList(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 更新购物车项数量
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  购买数量
     */
    void updateCartQuantity(Long userId, Long productId, Integer quantity);

}