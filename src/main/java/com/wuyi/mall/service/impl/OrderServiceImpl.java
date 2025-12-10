package com.wuyi.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyi.mall.entity.Cart;
import com.wuyi.mall.entity.Order;
import com.wuyi.mall.entity.OrderItem;
import com.wuyi.mall.entity.Product;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.CartMapper;
import com.wuyi.mall.mapper.OrderItemMapper;
import com.wuyi.mall.mapper.OrderMapper;
import com.wuyi.mall.mapper.ProductMapper;
import com.wuyi.mall.service.OrderService;
import com.wuyi.mall.util.OrderNoUtil;
import com.wuyi.mall.vo.OrderBatchResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

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
     * 购物车Mapper
     */
    @Autowired
    private CartMapper cartMapper;

    /**
     * 商品Mapper
     */
    @Autowired
    private ProductMapper productMapper;

    /**
     * 从购物车批量下单
     *
     * @param userId  用户ID
     * @param cartIds 购物车项ID列表
     * @return 批量下单结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderBatchResultVO batchFromCart(Long userId, List<Long> cartIds) {
        // 校验cartIds
        if (cartIds == null || cartIds.isEmpty()) {
            throw new GlobalExceptionHandler.BusinessException(400, "购物车项ID列表不能为空");
        }

        // 1. 根据用户ID和购物车项ID列表查询购物车项
        List<Cart> cartList = cartMapper.selectByUserIdAndIds(userId, cartIds);

        // 2. 构建购物车项ID到购物车项的映射
        Map<Long, Cart> cartMap = cartList.stream().collect(Collectors.toMap(Cart::getId, cart -> cart));

        // 3. 收集商品ID列表
        List<Long> productIds = cartList.stream().map(Cart::getProductId).collect(Collectors.toList());

        // 4. 批量查询商品信息
        List<Product> productList = productMapper.selectByIds(productIds);

        // 5. 构建商品ID到商品信息的映射
        Map<Long, Product> productMap = productList.stream().collect(Collectors.toMap(Product::getId, product -> product));

        // 6. 构建成功列表和失败列表
        List<Cart> successCarts = new ArrayList<>();
        List<OrderBatchResultVO.FailItem> failItems = new ArrayList<>();

        // 7. 遍历购物车项，进行校验
        for (Long cartId : cartIds) {
            Cart cart = cartMap.get(cartId);

            // 检查购物车项是否存在
            if (cart == null) {
                OrderBatchResultVO.FailItem failItem = new OrderBatchResultVO.FailItem();
                failItem.setCartId(cartId);
                failItem.setReason("购物车项不存在");
                failItems.add(failItem);
                continue;
            }

            // 检查购物车项是否归属当前用户
            if (!cart.getUserId().equals(userId)) {
                OrderBatchResultVO.FailItem failItem = new OrderBatchResultVO.FailItem();
                failItem.setCartId(cartId);
                failItem.setReason("无权操作他人购物车");
                failItems.add(failItem);
                continue;
            }

            Product product = productMap.get(cart.getProductId());

            // 检查商品是否存在
            if (product == null) {
                OrderBatchResultVO.FailItem failItem = new OrderBatchResultVO.FailItem();
                failItem.setCartId(cartId);
                failItem.setReason("商品不存在");
                failItems.add(failItem);
                continue;
            }

            // 检查商品是否已下架
            if (product.getStatus() != 1) {
                OrderBatchResultVO.FailItem failItem = new OrderBatchResultVO.FailItem();
                failItem.setCartId(cartId);
                failItem.setReason("商品已下架");
                failItems.add(failItem);
                continue;
            }

            // 检查商品库存是否足够
            if (product.getStockNum() < cart.getQuantity()) {
                OrderBatchResultVO.FailItem failItem = new OrderBatchResultVO.FailItem();
                failItem.setCartId(cartId);
                failItem.setReason("库存不足");
                failItems.add(failItem);
                continue;
            }

            // 添加到成功列表
            successCarts.add(cart);
        }

        // 8. 检查是否有成功的购物车项
        if (successCarts.isEmpty()) {
            throw new GlobalExceptionHandler.BusinessException(400, "无有效购物车项/所有商品均不满足下单条件");
        }

        // 9. 生成订单编号
        String orderNo = OrderNoUtil.generateOrderNo(userId);

        // 10. 计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : successCarts) {
            Product product = productMap.get(cart.getProductId());
            BigDecimal itemAmount = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(itemAmount);
        }

        // 11. 创建订单记录
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(1); // 客户下单
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        // 12. 保存订单记录
        orderMapper.insert(order);

        // 13. 创建订单详情记录
        List<OrderItem> orderItemList = new ArrayList<>();
        for (Cart cart : successCarts) {
            Product product = productMap.get(cart.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setCreateTime(LocalDateTime.now());

            orderItemList.add(orderItem);
        }

        // 14. 批量保存订单详情记录
        orderItemMapper.batchInsert(orderItemList);

        // 15. 扣减商品库存
        for (Cart cart : successCarts) {
            Product product = productMap.get(cart.getProductId());
            // 使用乐观锁扣减库存
            int result = productMapper.decreaseStock(product.getId(), cart.getQuantity(), product.getVersion());
            if (result != 1) {
                throw new GlobalExceptionHandler.BusinessException(500, "库存扣减失败，请重试");
            }
        }

        // 16. 批量删除已下单的购物车项
        List<Long> successCartIds = successCarts.stream().map(Cart::getId).collect(Collectors.toList());
        cartMapper.batchDeleteByIds(successCartIds);

        // 17. 构建并返回结果
        OrderBatchResultVO resultVO = new OrderBatchResultVO();
        resultVO.setOrderNo(orderNo);
        resultVO.setSuccessCount(successCarts.size());
        resultVO.setFailItems(failItems);

        return resultVO;
    }

}