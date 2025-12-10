package com.wuyi.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyi.mall.entity.Cart;
import com.wuyi.mall.entity.Collect;
import com.wuyi.mall.entity.Product;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.CartMapper;
import com.wuyi.mall.mapper.CollectMapper;
import com.wuyi.mall.mapper.ProductMapper;
import com.wuyi.mall.service.CollectService;
import com.wuyi.mall.vo.CollectBatchResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect> implements CollectService {

    /**
     * 收藏Mapper
     */
    @Autowired
    private CollectMapper collectMapper;

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
     * 从购物车批量添加到收藏
     *
     * @param userId  用户ID
     * @param cartIds 购物车项ID列表
     * @return 批量转收藏结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CollectBatchResultVO batchFromCart(Long userId, List<Long> cartIds) {
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

        // 6. 批量检查商品是否已被用户收藏
        List<Collect> collectedList = collectMapper.selectByUserIdAndProductIds(userId, productIds);

        // 7. 构建已收藏商品ID集合
        List<Long> collectedProductIds = collectedList.stream().map(Collect::getProductId).collect(Collectors.toList());

        // 8. 构建收藏记录列表和成功ID列表
        List<Collect> collectList = new ArrayList<>();
        List<Long> successCartIds = new ArrayList<>();
        List<CollectBatchResultVO.FailItem> failItems = new ArrayList<>();

        // 9. 遍历购物车项，处理每个购物车项
        for (Long cartId : cartIds) {
            Cart cart = cartMap.get(cartId);

            // 检查购物车项是否存在
            if (cart == null) {
                CollectBatchResultVO.FailItem failItem = new CollectBatchResultVO.FailItem();
                failItem.setCartId(cartId);
                failItem.setReason("购物车项不存在");
                failItems.add(failItem);
                continue;
            }

            // 检查购物车项是否归属当前用户
            if (!cart.getUserId().equals(userId)) {
                CollectBatchResultVO.FailItem failItem = new CollectBatchResultVO.FailItem();
                failItem.setCartId(cartId);
                failItem.setReason("无权操作他人购物车");
                failItems.add(failItem);
                continue;
            }

            Product product = productMap.get(cart.getProductId());

            // 检查商品是否存在
            if (product == null) {
                CollectBatchResultVO.FailItem failItem = new CollectBatchResultVO.FailItem();
                failItem.setCartId(cartId);
                failItem.setReason("商品不存在");
                failItems.add(failItem);
                continue;
            }

            // 检查商品是否已下架
            if (product.getStatus() != 1) {
                CollectBatchResultVO.FailItem failItem = new CollectBatchResultVO.FailItem();
                failItem.setCartId(cartId);
                failItem.setReason("商品已下架");
                failItems.add(failItem);
                continue;
            }

            // 检查商品是否已被收藏
            if (collectedProductIds.contains(product.getId())) {
                CollectBatchResultVO.FailItem failItem = new CollectBatchResultVO.FailItem();
                failItem.setCartId(cartId);
                failItem.setReason("商品已收藏");
                failItems.add(failItem);
                continue;
            }

            // 构建收藏记录
            Collect collect = new Collect();
            collect.setUserId(userId);
            collect.setProductId(product.getId());
            collect.setCreateTime(LocalDateTime.now());
            collectList.add(collect);
            successCartIds.add(cartId);
        }

        // 10. 新增收藏记录
        if (!collectList.isEmpty()) {
            collectMapper.batchInsert(collectList);
        }

        // 11. 删除已转收藏的购物车项
        if (!successCartIds.isEmpty()) {
            cartMapper.batchDeleteByIds(successCartIds);
        }

        // 12. 构建并返回结果
        CollectBatchResultVO resultVO = new CollectBatchResultVO();
        resultVO.setSuccessCount(successCartIds.size());
        resultVO.setFailItems(failItems);

        return resultVO;
    }

    /**
     * 检查商品是否已被用户收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    @Override
    public boolean isCollected(Long userId, Long productId) {
        Collect collect = collectMapper.selectByUserIdAndProductId(userId, productId);
        return collect != null;
    }

    /**
     * 批量检查商品是否已被用户收藏
     *
     * @param userId     用户ID
     * @param productIds 商品ID列表
     * @return 已收藏的商品ID列表
     */
    @Override
    public List<Long> batchIsCollected(Long userId, List<Long> productIds) {
        List<Collect> collectList = collectMapper.selectByUserIdAndProductIds(userId, productIds);
        return collectList.stream().map(Collect::getProductId).collect(Collectors.toList());
    }

}