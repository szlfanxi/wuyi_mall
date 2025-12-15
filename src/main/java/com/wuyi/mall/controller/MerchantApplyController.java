package com.wuyi.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.MerchantApply;
import com.wuyi.mall.service.MerchantApplyService;
import com.wuyi.mall.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 商家申请Controller
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/merchant-applies")
public class MerchantApplyController {

    @Autowired
    private MerchantApplyService merchantApplyService;

    /**
     * 提交商家入驻申请
     * 前置条件：无（未注册商家可提交申请，无需登录）
     *
     * @param merchantApply 商家申请信息
     * @return 申请结果
     */
    @PostMapping
    public ResultUtil.Result<Map<String, Object>> submitMerchantApply(@RequestBody MerchantApply merchantApply) {
        Map<String, Object> result = merchantApplyService.submitMerchantApply(merchantApply);
        return ResultUtil.success(result, "入驻申请提交成功");
    }

    /**
     * 查询商家申请列表（分页）
     * 前置条件：管理员已登录
     *
     * @param status     状态：0-待审核，1-已通过，2-已拒绝
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 商家申请列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil.Result<Page<MerchantApply>> getMerchantApplyList(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<MerchantApply> merchantApplyList = merchantApplyService.getMerchantApplyList(status, startTime, endTime, pageNum, pageSize);
        return ResultUtil.success(merchantApplyList);
    }

    /**
     * 根据ID查询商家申请
     * 前置条件：管理员已登录
     *
     * @param id 申请ID
     * @return 商家申请信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil.Result<MerchantApply> getMerchantApplyById(@PathVariable Long id) {
        MerchantApply merchantApply = merchantApplyService.getMerchantApplyById(id);
        return ResultUtil.success(merchantApply);
    }

}
