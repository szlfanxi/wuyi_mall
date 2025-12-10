package com.wuyi.mall.controller;

import com.wuyi.mall.service.OrderService;
import com.wuyi.mall.util.ResultUtil;
import com.wuyi.mall.vo.OrderBatchResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单控制器
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    /**
     * 订单服务
     */
    @Autowired
    private OrderService orderService;

    /**
     * 从购物车批量下单
     *
     * @param authentication 认证信息
     * @param cartIds        购物车项ID列表
     * @return 批量下单结果
     */
    @PostMapping("/batch")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResultUtil.Result<OrderBatchResultVO> batchFromCart(Authentication authentication, @RequestBody List<Long> cartIds) {
        // 从认证信息中获取用户ID
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 调用订单服务批量下单
        OrderBatchResultVO resultVO = orderService.batchFromCart(userId, cartIds);

        // 返回下单成功结果
        return ResultUtil.success(resultVO);
    }

}