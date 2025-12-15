package com.wuyi.mall.controller;

import com.wuyi.mall.service.OrderStatusService;
import com.wuyi.mall.util.ResultUtil;
import com.wuyi.mall.vo.OrderOperateResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单状态控制器
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/api/orders")
public class OrderStatusController {

    /**
     * 订单状态服务
     */
    @Autowired
    private OrderStatusService orderStatusService;

    /**
     * 订单状态流转操作（商家）
     *
     * @param authentication 认证信息
     * @param orderId        订单ID
     * @param requestBody    请求体，包含operateType
     * @return 操作结果
     */
    @PostMapping("/{orderId}/operate")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResultUtil.Result<OrderOperateResultVO> operateOrder(Authentication authentication, @PathVariable Long orderId, @RequestBody Map<String, String> requestBody) {
        // 从认证信息中获取商家ID
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 获取操作类型
        String operateType = requestBody.get("operateType");
        if (operateType == null || operateType.isEmpty()) {
            throw new IllegalArgumentException("操作类型不能为空");
        }

        // 调用订单状态服务进行操作
        OrderOperateResultVO resultVO = orderStatusService.operateOrder(orderId, operateType, userId);

        // 返回操作结果
        return ResultUtil.success(resultVO);
    }

    /**
     * 客户取消订单
     *
     * @param authentication 认证信息
     * @param orderId        订单ID
     * @return 操作结果
     */
    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResultUtil.Result<OrderOperateResultVO> cancelOrderByCustomer(Authentication authentication, @PathVariable Long orderId) {
        // 从认证信息中获取客户ID
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 调用订单状态服务取消订单
        OrderOperateResultVO resultVO = orderStatusService.cancelOrderByCustomer(orderId, userId);

        // 返回操作结果
        return ResultUtil.success(resultVO);
    }

    /**
     * 商家取消订单
     *
     * @param authentication 认证信息
     * @param orderId        订单ID
     * @return 操作结果
     */
    @PostMapping("/{orderId}/cancel-by-merchant")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResultUtil.Result<OrderOperateResultVO> cancelOrderByMerchant(Authentication authentication, @PathVariable Long orderId) {
        // 从认证信息中获取商家ID
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 调用订单状态服务取消订单
        OrderOperateResultVO resultVO = orderStatusService.cancelOrderByMerchant(orderId, userId);

        // 返回操作结果
        return ResultUtil.success(resultVO);
    }

}