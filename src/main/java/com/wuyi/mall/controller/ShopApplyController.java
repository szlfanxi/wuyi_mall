package com.wuyi.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.ShopApply;
import com.wuyi.mall.service.ShopApplyService;
import com.wuyi.mall.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 商铺申请Controller
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/shop-applies")
public class ShopApplyController {

    @Autowired
    private ShopApplyService shopApplyService;

    /**
     * 提交商铺创建申请
     * 前置条件：商家已登录（user.role_type=2），请求头携带有效Authorization
     *
     * @param authentication 认证信息
     * @param shopApply 商铺申请信息
     * @return 申请结果
     */
    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public ResultUtil.Result<Map<String, Object>> submitShopApply(Authentication authentication, @RequestBody ShopApply shopApply) {
        // 从认证信息中获取商家ID
        Long merchantId = Long.parseLong(authentication.getPrincipal().toString());
        shopApply.setMerchantId(merchantId);

        Map<String, Object> result = shopApplyService.submitShopApply(shopApply);
        return ResultUtil.success(result, "商铺申请提交成功");
    }

    /**
     * 商家查询自身申请记录
     * 前置条件：商家已登录（user.role_type=2），请求头携带有效Authorization
     *
     * @param authentication 认证信息
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 商铺申请列表
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResultUtil.Result<Page<ShopApply>> getMyShopApplies(Authentication authentication, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        // 从认证信息中获取商家ID
        Long merchantId = Long.parseLong(authentication.getPrincipal().toString());

        Page<ShopApply> shopApplyList = shopApplyService.getShopApplyListByMerchant(merchantId, pageNum, pageSize);
        return ResultUtil.success(shopApplyList);
    }

    /**
     * 查询商铺申请列表（分页）
     * 前置条件：管理员已登录
     *
     * @param status     状态：0-待审核，1-已通过，2-已拒绝
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 商铺申请列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil.Result<Page<ShopApply>> getShopApplyList(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ShopApply> shopApplyList = shopApplyService.getShopApplyList(status, startTime, endTime, pageNum, pageSize);
        return ResultUtil.success(shopApplyList);
    }

    /**
     * 根据ID查询商铺申请
     * 前置条件：管理员已登录
     *
     * @param id 申请ID
     * @return 商铺申请信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil.Result<ShopApply> getShopApplyById(@PathVariable Long id) {
        ShopApply shopApply = shopApplyService.getShopApplyById(id);
        return ResultUtil.success(shopApply);
    }

}
