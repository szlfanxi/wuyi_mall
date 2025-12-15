package com.wuyi.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.PaymentRecord;
import com.wuyi.mall.service.PayService;
import com.wuyi.mall.util.ResultUtil;
import com.wuyi.mall.vo.PayRequestVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付控制器
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/pays")
public class PayController {

    /**
     * 支付服务
     */
    @Autowired
    private PayService payService;

    /**
     * 客户发起支付
     *
     * @param authentication 认证信息
     * @param payRequestVO 支付请求参数
     * @return 支付结果
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResultUtil.Result<Map<String, Object>> initiatePay(Authentication authentication, @Valid @RequestBody PayRequestVO payRequestVO) {
        // 从认证信息中获取用户ID
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 调用支付服务发起支付
        Map<String, Object> result = payService.initiatePay(userId, payRequestVO);

        return ResultUtil.success(result);
    }

    /**
     * 客户查询自身支付记录
     *
     * @param authentication 认证信息
     * @param orderId 订单ID（可选）
     * @param payType 支付方式（可选）
     * @param startTime 开始时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime 结束时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 支付记录列表（分页）
     */
    @GetMapping("/records")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResultUtil.Result<Page<PaymentRecord>> getUserPayRecords(Authentication authentication, @RequestParam(required = false) Long orderId, @RequestParam(required = false) String payType, @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        // 从认证信息中获取用户ID
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 调用支付服务查询支付记录
        Page<PaymentRecord> records = payService.getUserPayRecords(userId, orderId, payType, startTime, endTime, pageNum, pageSize);

        return ResultUtil.success(records);
    }

    /**
     * 商家查询自家订单支付记录
     *
     * @param authentication 认证信息
     * @param shopId 商铺ID（必填）
     * @param orderNo 订单编号（可选）
     * @param payStatus 支付状态（可选）
     * @param startTime 开始时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime 结束时间（可选，格式：yyyy-MM-dd HH:mm:ss）
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 支付记录列表（分页）
     */
    @GetMapping("/merchant/records")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResultUtil.Result<Page<PaymentRecord>> getMerchantPayRecords(Authentication authentication, @RequestParam Long shopId, @RequestParam(required = false) String orderNo, @RequestParam(required = false) Integer payStatus, @RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        // 调用支付服务查询支付记录
        Page<PaymentRecord> records = payService.getMerchantPayRecords(shopId, orderNo, payStatus, startTime, endTime, pageNum, pageSize);

        return ResultUtil.success(records);
    }

    /**
     * 管理员查询全平台支付记录
     *
     * @param params 查询参数，包含userId、shopId、payType、payStatus、时间范围等
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 支付记录列表（分页）
     */
    @GetMapping("/admin/records")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil.Result<Page<PaymentRecord>> getAdminPayRecords(@RequestParam Map<String, Object> params, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        // 调用支付服务查询支付记录
        Page<PaymentRecord> records = payService.getAdminPayRecords(params, pageNum, pageSize);

        return ResultUtil.success(records);
    }

    /**
     * 根据订单ID查询支付记录
     *
     * @param orderId 订单ID
     * @return 支付记录
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResultUtil.Result<PaymentRecord> getPaymentByOrderId(@PathVariable Long orderId) {
        // 调用支付服务查询支付记录
        PaymentRecord record = payService.getPaymentByOrderId(orderId);

        return ResultUtil.success(record);
    }

}