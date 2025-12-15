package com.wuyi.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyi.mall.entity.Cart;
import com.wuyi.mall.entity.Product;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.CartMapper;
import com.wuyi.mall.service.CartService;
import com.wuyi.mall.service.ProductService;
import com.wuyi.mall.vo.CartVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    /**
     * 购物车Mapper
     */
    @Autowired
    private CartMapper cartMapper;

    /**
     * 商品服务
     */
    @Autowired
    private ProductService productService;

    /**
     * 添加商品到购物车
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  购买数量
     * @return 购物车项ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addToCart(Long userId, Long productId, Integer quantity) {
        // 校验购买数量
        if (quantity < 1) {
            throw new GlobalExceptionHandler.BusinessException(400, "购买数量不能少于1");
        }

        // 检查商品状态和库存
        Product product = productService.checkProductStatus(productId);
        productService.checkProductStock(productId, quantity);

        // 检查购物车中是否已存在该商品
        Cart existingCart = cartMapper.selectByUserIdAndProductId(userId, productId);

        if (existingCart != null) {
            // 计算新的购买数量
            Integer newQuantity = existingCart.getQuantity() + quantity;

            // 检查新的购买数量是否超过库存
            productService.checkProductStock(productId, newQuantity);

            // 更新购物车项数量
            existingCart.setQuantity(newQuantity);
            existingCart.setUpdateTime(LocalDateTime.now());
            cartMapper.updateById(existingCart);

            return existingCart.getId();
        } else {
            // 创建新的购物车项
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());

            // 保存购物车项
            cartMapper.insert(cart);

            return cart.getId();
        }
    }

    /**
     * 查询用户购物车列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 购物车列表
     */
    @Override
    public List<CartVO> getCartList(Long userId, Integer pageNum, Integer pageSize) {
        // 查询用户的购物车项
        List<Cart> cartList = cartMapper.selectByUserId(userId);

        // 构建购物车返回列表
        List<CartVO> cartVOList = new ArrayList<>();

        for (Cart cart : cartList) {
            // 查询商品信息
            Product product = productService.getProductById(cart.getProductId());

            // 跳过已下架或已删除的商品
            if (product == null || product.getStatus() != 1 || product.getDeleted() != 0) {
                continue;
            }

            // 构建购物车返回对象
            CartVO cartVO = new CartVO();
            BeanUtils.copyProperties(cart, cartVO);
            BeanUtils.copyProperties(product, cartVO);

            // 设置库存状态
            boolean stockStatus = product.getStockNum() >= cart.getQuantity();
            cartVO.setStockStatus(stockStatus);

            // 添加到返回列表
            cartVOList.add(cartVO);
        }

        return cartVOList;
    }

    /**
     * 更新购物车项数量
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  购买数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCartQuantity(Long userId, Long productId, Integer quantity) {
        // 校验购买数量
        if (quantity < 1) {
            throw new GlobalExceptionHandler.BusinessException(400, "购买数量不能少于1");
        }

        // 检查商品状态和库存
        productService.checkProductStatus(productId);
        productService.checkProductStock(productId, quantity);

        // 检查购物车中是否已存在该商品
        Cart existingCart = cartMapper.selectByUserIdAndProductId(userId, productId);

        if (existingCart != null) {
            // 更新购物车项数量
            existingCart.setQuantity(quantity);
            existingCart.setUpdateTime(LocalDateTime.now());
            cartMapper.updateById(existingCart);
        } else {
            throw new GlobalExceptionHandler.BusinessException(400, "购物车中不存在该商品");
        }
    }

}