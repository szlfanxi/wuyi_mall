package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyi.mall.entity.OrderOperateLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作日志Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface OrderOperateLogMapper extends BaseMapper<OrderOperateLog> {

}