package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.MerchantApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 商家申请Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface MerchantApplyMapper extends BaseMapper<MerchantApply> {

    /**
     * 根据状态查询商家申请列表（分页）
     *
     * @param page       分页参数
     * @param status     状态：0-待审核，1-已通过，2-已拒绝
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 商家申请列表
     */
    Page<MerchantApply> selectByStatus(Page<MerchantApply> page, @Param("status") Integer status, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 检查商家名称是否已存在
     *
     * @param merchantName 商家名称
     * @return 存在数量
     */
    int checkMerchantNameExists(@Param("merchantName") String merchantName);

    /**
     * 检查营业执照编号是否已存在
     *
     * @param businessLicense 营业执照编号
     * @return 存在数量
     */
    int checkBusinessLicenseExists(@Param("businessLicense") String businessLicense);

}
