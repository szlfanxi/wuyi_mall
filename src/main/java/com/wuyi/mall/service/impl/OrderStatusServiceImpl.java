package com.wuyi.mall.service.impl;

import com.wuyi.mall.entity.Order;
import com.wuyi.mall.entity.OrderItem;
import com.wuyi.mall.entity.OrderOperateLog;
import com.wuyi.mall.entity.Product;
import com.wuyi.mall.enums.OrderOperateTypeEnum;
import com.wuyi.mall.enums.OrderStatusEnum;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.OrderItemMapper;
import com.wuyi.mall.mapper.OrderMapper;
import com.wuyi.mall.mapper.OrderOperateLogMapper;
import com.wuyi.mall.mapper.ProductMapper;
import com.wuyi.mall.service.OrderStatusService;
import com.wuyi.mall.vo.OrderOperateResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单状态服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class OrderStatusServiceImpl implements OrderStatusService {

    /**
     * 订单Mapper
     */
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 订单详情Mapper
     */
    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 订单操作日志Mapper
     */
    @Autowired
    private OrderOperateLogMapper orderOperateLogMapper;

    /**
     * 商品Mapper
     */
    @Autowired
    private ProductMapper productMapper;

    /**
     * 状态流转映射，key为当前状态+操作类型，value为目标状态
     */
    private static final Map<String, Integer> STATUS_TRANSITION_MAP = new HashMap<>();

    // 初始化状态流转规则
    static {
        // 客户下单(1) → 确认(CONFIRM) → 商家确认(2)
        STATUS_TRANSITION_MAP.put("1_CONFIRM", 2);
        // 商家确认(2) → 备货(PREPARE) → 备货完成(3)
        STATUS_TRANSITION_MAP.put("2_PREPARE", 3);
        // 备货完成(3) → 发货(DELIVER) → 已发货(4)
        STATUS_TRANSITION_MAP.put("3_DELIVER", 4);
    }

    /**
     * 订单状态流转操作
     *
     * @param orderId     订单ID
     * @param operateType 操作类型
     * @param userId      操作人ID
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderOperateResultVO operateOrder(Long orderId, String operateType, Long userId) {
        // 1. 校验操作类型
        OrderOperateTypeEnum operateTypeEnum = OrderOperateTypeEnum.getByCode(operateType);
        if (operateTypeEnum == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "操作类型无效");
        }

        // 2. 查询订单信息（带行锁）
        Order order = orderMapper.selectOrderByIdWithLock(orderId);
        if (order == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单不存在");
        }

        // 3. 校验订单是否已取消
        if (order.getStatus() == OrderStatusEnum.CANCEL.getCode()) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单已取消，无法操作");
        }

        // 4. 校验订单是否归属当前商家
        int merchantCount = orderMapper.checkOrderBelongToMerchant(orderId, userId);
        if (merchantCount == 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单不归属当前商家");
        }

        // 5. 校验订单状态是否允许该操作
        if (!checkOrderStatusCanOperate(order.getStatus(), operateType)) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单状态不允许该操作");
        }

        // 6. 获取目标状态
        Integer targetStatus = STATUS_TRANSITION_MAP.get(order.getStatus() + "_" + operateType);
        if (targetStatus == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单状态不允许该操作");
        }

        // 7. 保存旧状态
        Integer beforeStatus = order.getStatus();

        // 8. 更新订单状态
        int updateCount = orderMapper.updateStatusById(orderId, targetStatus, beforeStatus);
        if (updateCount != 1) {
            throw new GlobalExceptionHandler.BusinessException(500, "订单状态更新失败，请重试");
        }

        // 9. 新增订单操作日志
        OrderOperateLog log = new OrderOperateLog();
        log.setOrderId(orderId);
        log.setOperateUserId(userId);
        log.setOperateType(operateType);
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(targetStatus);
        log.setOperateTime(LocalDateTime.now());
        orderOperateLogMapper.insert(log);

        // 10. 构建返回结果
        OrderOperateResultVO result = new OrderOperateResultVO();
        result.setOrderId(orderId);
        result.setCurrentStatus(targetStatus);
        result.setStatusName(OrderStatusEnum.getByCode(targetStatus).getName());

        return result;
    }

    /**
     * 客户取消订单
     *
     * @param orderId 订单ID
     * @param userId  客户ID
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderOperateResultVO cancelOrderByCustomer(Long orderId, Long userId) {
        // 1. 查询订单信息（带行锁）
        Order order = orderMapper.selectOrderByIdWithLock(orderId);
        if (order == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单不存在");
        }

        // 2. 校验订单是否归属当前客户
        if (!order.getUserId().equals(userId)) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单不归属当前客户");
        }

        // 3. 校验订单状态是否允许取消（下单/确认/备货状态可取消）
        Integer currentStatus = order.getStatus();
        if (currentStatus == OrderStatusEnum.CANCEL.getCode()) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单已取消");
        }
        if (currentStatus >= OrderStatusEnum.DELIVER.getCode()) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单已发货，无法取消");
        }

        // 4. 查询订单详情
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("order_id", orderId);
        List<OrderItem> orderItemList = orderItemMapper.selectByMap(columnMap);
        if (orderItemList.isEmpty()) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单详情不存在");
        }

        // 5. 恢复商品库存
        for (OrderItem orderItem : orderItemList) {
            // 查询商品信息
            Product product = productMapper.selectById(orderItem.getProductId());
            if (product == null) {
                throw new GlobalExceptionHandler.BusinessException(400, "商品不存在");
            }

            // 恢复库存（乐观锁）
            int result = productMapper.increaseStock(product.getId(), orderItem.getQuantity(), product.getVersion());
            if (result != 1) {
                throw new GlobalExceptionHandler.BusinessException(500, "库存恢复失败，请重试");
            }
        }

        // 6. 更新订单状态为取消
        int updateCount = orderMapper.updateStatusById(orderId, OrderStatusEnum.CANCEL.getCode(), currentStatus);
        if (updateCount != 1) {
            throw new GlobalExceptionHandler.BusinessException(500, "订单状态更新失败，请重试");
        }

        // 7. 新增订单操作日志
        OrderOperateLog log = new OrderOperateLog();
        log.setOrderId(orderId);
        log.setOperateUserId(userId);
        log.setOperateType(OrderOperateTypeEnum.CANCEL_BY_CUSTOMER.getCode());
        log.setBeforeStatus(currentStatus);
        log.setAfterStatus(OrderStatusEnum.CANCEL.getCode());
        log.setOperateTime(LocalDateTime.now());
        orderOperateLogMapper.insert(log);

        // 8. 构建返回结果
        OrderOperateResultVO result = new OrderOperateResultVO();
        result.setOrderId(orderId);
        result.setCurrentStatus(OrderStatusEnum.CANCEL.getCode());
        result.setStatusName(OrderStatusEnum.CANCEL.getName());

        return result;
    }

    /**
     * 商家取消订单
     *
     * @param orderId 订单ID
     * @param userId  商家ID
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderOperateResultVO cancelOrderByMerchant(Long orderId, Long userId) {
        // 1. 查询订单信息（带行锁）
        Order order = orderMapper.selectOrderByIdWithLock(orderId);
        if (order == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单不存在");
        }

        // 2. 校验订单是否已取消
        if (order.getStatus() == OrderStatusEnum.CANCEL.getCode()) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单已取消");
        }

        // 3. 校验订单是否归属当前商家
        int merchantCount = orderMapper.checkOrderBelongToMerchant(orderId, userId);
        if (merchantCount == 0) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单不归属当前商家");
        }

        // 4. 校验订单状态是否允许取消（完成前可取消）
        if (order.getStatus() == OrderStatusEnum.COMPLETE.getCode()) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单已完成，无法取消");
        }

        // 5. 查询订单详情
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("order_id", orderId);
        List<OrderItem> orderItemList = orderItemMapper.selectByMap(columnMap);
        if (orderItemList.isEmpty()) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单详情不存在");
        }

        // 6. 恢复商品库存
        for (OrderItem orderItem : orderItemList) {
            // 查询商品信息
            Product product = productMapper.selectById(orderItem.getProductId());
            if (product == null) {
                throw new GlobalExceptionHandler.BusinessException(400, "商品不存在");
            }

            // 恢复库存（乐观锁）
            int result = productMapper.increaseStock(product.getId(), orderItem.getQuantity(), product.getVersion());
            if (result != 1) {
                throw new GlobalExceptionHandler.BusinessException(500, "库存恢复失败，请重试");
            }
        }

        // 7. 更新订单状态为取消
        int updateCount = orderMapper.updateStatusById(orderId, OrderStatusEnum.CANCEL.getCode(), order.getStatus());
        if (updateCount != 1) {
            throw new GlobalExceptionHandler.BusinessException(500, "订单状态更新失败，请重试");
        }

        // 8. 新增订单操作日志
        OrderOperateLog log = new OrderOperateLog();
        log.setOrderId(orderId);
        log.setOperateUserId(userId);
        log.setOperateType(OrderOperateTypeEnum.CANCEL_BY_MERCHANT.getCode());
        log.setBeforeStatus(order.getStatus());
        log.setAfterStatus(OrderStatusEnum.CANCEL.getCode());
        log.setOperateTime(LocalDateTime.now());
        orderOperateLogMapper.insert(log);

        // 9. 构建返回结果
        OrderOperateResultVO result = new OrderOperateResultVO();
        result.setOrderId(orderId);
        result.setCurrentStatus(OrderStatusEnum.CANCEL.getCode());
        result.setStatusName(OrderStatusEnum.CANCEL.getName());

        return result;
    }

    /**
     * 检查订单状态是否允许操作
     *
     * @param currentStatus 当前状态
     * @param operateType   操作类型
     * @return 允许返回true，不允许返回false
     */
    @Override
    public boolean checkOrderStatusCanOperate(Integer currentStatus, String operateType) {
        // 检查状态流转规则中是否存在该操作
        String key = currentStatus + "_" + operateType;
        return STATUS_TRANSITION_MAP.containsKey(key);
    }

}