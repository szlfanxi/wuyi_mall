package com.wuyi.mall.controller;

import com.wuyi.mall.service.CartService;
import com.wuyi.mall.util.ResultUtil;
import com.wuyi.mall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    /**
     * 购物车服务
     */
    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     *
     * @param authentication 认证信息
     * @param productId      商品ID
     * @param quantity       购买数量
     * @return 添加结果
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResultUtil.Result<Long> addToCart(Authentication authentication, @RequestParam Long productId, @RequestParam Integer quantity) {
        // 从认证信息中获取用户ID
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 调用购物车服务添加商品
        Long cartId = cartService.addToCart(userId, productId, quantity);

        // 返回添加成功结果
        return ResultUtil.success(cartId);
    }

    /**
     * 查询购物车列表
     *
     * @param authentication 认证信息
     * @param pageNum        页码，默认1
     * @param pageSize       每页条数，默认10
     * @return 购物车列表
     */
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResultUtil.Result<List<CartVO>> getCartList(Authentication authentication, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        // 从认证信息中获取用户ID
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 调用购物车服务查询购物车列表
        List<CartVO> cartVOList = cartService.getCartList(userId, pageNum, pageSize);

        // 返回查询成功结果
        return ResultUtil.success(cartVOList);
    }

}