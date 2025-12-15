package com.wuyi.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.service.ProductPriceService;
import com.wuyi.mall.util.ResultUtil;
import com.wuyi.mall.vo.ProductPriceLogVO;
import com.wuyi.mall.vo.ProductPriceModifyVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商品价格管理控制器
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/products")
public class ProductPriceController {

    /**
     * 商品价格管理服务
     */
    @Autowired
    private ProductPriceService productPriceService;

    /**
     * 修改商品价格（商家权限）
     *
     * @param authentication       认证信息
     * @param productPriceModifyVO 修改价格请求参数
     * @return 修改结果
     */
    @PostMapping("/price")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResultUtil.Result<Map<String, Object>> modifyProductPrice(Authentication authentication, @Valid @RequestBody ProductPriceModifyVO productPriceModifyVO) {
        // 从认证信息中获取用户ID（商家ID）
        Long operateUserId = Long.parseLong(authentication.getPrincipal().toString());
        // 这里假设商家ID就是shopId，实际项目中可能需要从用户关联的shop表中查询
        Long shopId = operateUserId;

        // 调用服务修改商品价格
        Map<String, Object> result = productPriceService.modifyProductPrice(shopId, operateUserId, productPriceModifyVO);

        return ResultUtil.success(result);
    }

    /**
     * 查询商家商品价格变动日志（商家权限）
     *
     * @param authentication 认证信息
     * @param productId      商品ID
     * @param pageNum        页码（默认1）
     * @param pageSize       每页条数（默认10）
     * @param startTime      开始时间（可选）
     * @param endTime        结束时间（可选）
     * @return 价格变动日志列表（分页）
     */
    @GetMapping("/price/logs")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResultUtil.Result<Page<ProductPriceLogVO>> getMerchantPriceLogs(Authentication authentication,
                                                               @RequestParam Long productId,
                                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                                               @RequestParam(required = false) String startTime,
                                                               @RequestParam(required = false) String endTime) {
        // 从认证信息中获取用户ID（商家ID）
        Long operateUserId = Long.parseLong(authentication.getPrincipal().toString());
        // 这里假设商家ID就是shopId，实际项目中可能需要从用户关联的shop表中查询
        Long shopId = operateUserId;

        // 调用服务查询价格变动日志
        Page<ProductPriceLogVO> logs = productPriceService.getMerchantPriceLogs(shopId, productId, pageNum, pageSize, startTime, endTime);

        return ResultUtil.success(logs);
    }

    /**
     * 查询管理员价格变动日志（管理员权限，支持多条件筛选）
     *
     * @param pageNum        页码（默认1）
     * @param pageSize       每页条数（默认10）
     * @param productId      商品ID（可选）
     * @param shopId         商铺ID（可选）
     * @param operateUserId  操作人ID（可选）
     * @param startTime      开始时间（可选）
     * @param endTime        结束时间（可选）
     * @return 价格变动日志列表（分页）
     */
    @GetMapping("/admin/price/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil.Result<Page<ProductPriceLogVO>> getAdminPriceLogs(@RequestParam(defaultValue = "1") Integer pageNum,
                                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                                           @RequestParam(required = false) Long productId,
                                                           @RequestParam(required = false) Long shopId,
                                                           @RequestParam(required = false) Long operateUserId,
                                                           @RequestParam(required = false) String startTime,
                                                           @RequestParam(required = false) String endTime) {
        // 调用服务查询价格变动日志
        Page<ProductPriceLogVO> logs = productPriceService.getAdminPriceLogs(pageNum, pageSize, productId, shopId, operateUserId, startTime, endTime);

        return ResultUtil.success(logs);
    }

}