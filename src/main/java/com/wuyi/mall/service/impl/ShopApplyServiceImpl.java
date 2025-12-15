package com.wuyi.mall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.ShopApply;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.ShopApplyMapper;
import com.wuyi.mall.service.ShopApplyService;
import com.wuyi.mall.util.DesensitizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 商铺申请服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class ShopApplyServiceImpl implements ShopApplyService {

    @Autowired
    private ShopApplyMapper shopApplyMapper;

    /**
     * 提交商铺创建申请
     *
     * @param shopApply 商铺申请信息
     * @return 申请结果，包含shopApplyId
     */
    @Override
    public Map<String, Object> submitShopApply(ShopApply shopApply) {
        // 校验商铺名称是否已存在
        int shopNameCount = shopApplyMapper.checkShopNameExists(shopApply.getShopName());
        if (shopNameCount > 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "商铺名称已存在");
        }

        // 校验手机号格式
        if (!DesensitizeUtil.isValidPhone(shopApply.getContactPhone())) {
            throw new GlobalExceptionHandler.BusinessException(400, "联系电话格式错误");
        }

        // 校验分类ID是否存在
        // 这里简化处理，实际项目中需要查询分类表确认分类ID是否存在

        // 校验商家是否已有待审核的商铺申请
        int pendingCount = shopApplyMapper.countPendingByMerchantId(shopApply.getMerchantId());
        if (pendingCount > 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "已有待审核的商铺申请，请耐心等待");
        }

        // 设置默认值
        shopApply.setStatus(0); // 待审核
        shopApply.setCreateTime(LocalDateTime.now());

        // 保存商铺申请
        shopApplyMapper.insert(shopApply);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("shopApplyId", shopApply.getId());
        return result;
    }

    /**
     * 查询商家的商铺申请列表（分页）
     *
     * @param merchantId 商家ID
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 商铺申请列表
     */
    @Override
    public Page<ShopApply> getShopApplyListByMerchant(Long merchantId, Integer pageNum, Integer pageSize) {
        Page<ShopApply> page = new Page<>(pageNum, pageSize);
        return shopApplyMapper.selectByMerchantId(page, merchantId);
    }

    /**
     * 查询商铺申请列表（分页）
     *
     * @param status     状态：0-待审核，1-已通过，2-已拒绝
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param pageNum    页码
     * @param pageSize   每页条数
     * @return 商铺申请列表
     */
    @Override
    public Page<ShopApply> getShopApplyList(Integer status, LocalDateTime startTime, LocalDateTime endTime, Integer pageNum, Integer pageSize) {
        Page<ShopApply> page = new Page<>(pageNum, pageSize);
        return shopApplyMapper.selectByStatus(page, status, startTime, endTime);
    }

    /**
     * 根据ID查询商铺申请
     *
     * @param id 申请ID
     * @return 商铺申请信息
     */
    @Override
    public ShopApply getShopApplyById(Long id) {
        return shopApplyMapper.selectById(id);
    }

}
