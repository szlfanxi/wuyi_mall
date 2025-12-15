package com.wuyi.mall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.MerchantApply;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.MerchantApplyMapper;
import com.wuyi.mall.service.MerchantApplyService;
import com.wuyi.mall.util.DesensitizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 商家申请服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class MerchantApplyServiceImpl implements MerchantApplyService {

    @Autowired
    private MerchantApplyMapper merchantApplyMapper;

    /**
     * 提交商家入驻申请
     *
     * @param merchantApply 商家申请信息
     * @return 申请结果，包含applyId
     */
    @Override
    public Map<String, Object> submitMerchantApply(MerchantApply merchantApply) {
        // 校验商家名称是否已存在
        int merchantNameCount = merchantApplyMapper.checkMerchantNameExists(merchantApply.getMerchantName());
        if (merchantNameCount > 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "商家名称已存在");
        }

        // 校验营业执照编号是否已存在
        int businessLicenseCount = merchantApplyMapper.checkBusinessLicenseExists(merchantApply.getBusinessLicense());
        if (businessLicenseCount > 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "营业执照编号已存在");
        }

        // 校验手机号格式
        if (!DesensitizeUtil.isValidPhone(merchantApply.getPhone())) {
            throw new GlobalExceptionHandler.BusinessException(400, "手机号格式错误");
        }

        // 校验邮箱格式
        if (!DesensitizeUtil.isValidEmail(merchantApply.getEmail())) {
            throw new GlobalExceptionHandler.BusinessException(400, "邮箱格式错误");
        }

        // 校验身份证号格式
        if (!DesensitizeUtil.isValidIdCard(merchantApply.getPhone())) {
            throw new GlobalExceptionHandler.BusinessException(400, "身份证号格式错误");
        }

        // 脱敏处理敏感信息
        merchantApply.setIdCard(DesensitizeUtil.desensitizeIdCard(merchantApply.getIdCard()));
        merchantApply.setBankAccount(DesensitizeUtil.desensitizeBankAccount(merchantApply.getBankAccount()));

        // 设置默认值
        merchantApply.setStatus(0); // 待审核
        merchantApply.setCreateTime(LocalDateTime.now());

        // 保存商家申请
        merchantApplyMapper.insert(merchantApply);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("applyId", merchantApply.getId());
        return result;
    }

    /**
     * 查询商家申请列表（分页）
     *
     * @param status     状态：0-待审核，1-已通过，2-已拒绝
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 商家申请列表
     */
    @Override
    public Page<MerchantApply> getMerchantApplyList(Integer status, LocalDateTime startTime, LocalDateTime endTime, Integer pageNum, Integer pageSize) {
        Page<MerchantApply> page = new Page<>(pageNum, pageSize);
        return merchantApplyMapper.selectByStatus(page, status, startTime, endTime);
    }

    /**
     * 根据ID查询商家申请
     *
     * @param id 申请ID
     * @return 商家申请信息
     */
    @Override
    public MerchantApply getMerchantApplyById(Long id) {
        return merchantApplyMapper.selectById(id);
    }

}
