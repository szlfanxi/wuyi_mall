package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyi.mall.entity.PaymentConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 支付配置Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface PaymentConfigMapper extends BaseMapper<PaymentConfig> {

    /**
     * 根据支付方式查询支付配置
     *
     * @param payType 支付方式
     * @return 支付配置
     */
    PaymentConfig selectByPayType(@Param("payType") String payType);

    /**
     * 根据支付方式和状态查询支付配置
     *
     * @param payType 支付方式
     * @param status  状态：0-禁用，1-启用
     * @return 支付配置
     */
    PaymentConfig selectByPayTypeAndStatus(@Param("payType") String payType, @Param("status") Integer status);
}
