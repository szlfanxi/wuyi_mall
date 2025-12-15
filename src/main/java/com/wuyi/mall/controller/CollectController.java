package com.wuyi.mall.controller;

import com.wuyi.mall.service.CollectService;
import com.wuyi.mall.util.ResultUtil;
import com.wuyi.mall.vo.CollectBatchResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 收藏控制器
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/api/collects")
public class CollectController {

    /**
     * 收藏服务
     */
    @Autowired
    private CollectService collectService;

    /**
     * 从购物车批量添加到收藏
     *
     * @param authentication 认证信息
     * @param cartIds        购物车项ID列表
     * @return 批量转收藏结果
     */
    @PostMapping("/batch-from-cart")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResultUtil.Result<CollectBatchResultVO> batchFromCart(Authentication authentication, @RequestBody List<Long> cartIds) {
        // 从认证信息中获取用户ID
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 调用收藏服务批量转收藏
        CollectBatchResultVO resultVO = collectService.batchFromCart(userId, cartIds);

        // 返回转收藏成功结果
        return ResultUtil.success(resultVO);
    }

}