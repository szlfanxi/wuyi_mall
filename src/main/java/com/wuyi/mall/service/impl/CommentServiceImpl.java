package com.wuyi.mall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.Comment;
import com.wuyi.mall.entity.Order;
import com.wuyi.mall.entity.OrderItem;
import com.wuyi.mall.entity.Product;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.CommentMapper;
import com.wuyi.mall.mapper.OrderItemMapper;
import com.wuyi.mall.mapper.OrderMapper;
import com.wuyi.mall.mapper.ProductMapper;
import com.wuyi.mall.service.CommentService;
import com.wuyi.mall.vo.CommentRequestVO;
import com.wuyi.mall.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评价服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class CommentServiceImpl implements CommentService {

    /**
     * 评价Mapper
     */
    @Autowired
    private CommentMapper commentMapper;

    /**
     * 订单详情Mapper
     */
    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 订单Mapper
     */
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 商品Mapper
     */
    @Autowired
    private ProductMapper productMapper;

    /**
     * 发布商品评价
     *
     * @param userId           用户ID
     * @param commentRequestVO 评价请求参数
     * @return 评价ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishComment(Long userId, CommentRequestVO commentRequestVO) {
        // 1. 检查订单详情是否可评价
        if (!checkCommentAble(userId, commentRequestVO.getOrderItemId())) {
            throw new GlobalExceptionHandler.BusinessException(400, "该商品不可评价");
        }

        // 2. 查询订单详情信息
        OrderItem orderItem = orderItemMapper.selectById(commentRequestVO.getOrderItemId());
        if (orderItem == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单详情不存在");
        }

        // 3. 构建评价实体
        Comment comment = new Comment();
        comment.setOrderItemId(commentRequestVO.getOrderItemId());
        comment.setProductId(orderItem.getProductId());
        comment.setUserId(userId);
        comment.setContent(commentRequestVO.getContent());
        comment.setScore(commentRequestVO.getScore());
        comment.setImages(convertImagesToString(commentRequestVO.getImages()));
        comment.setCreateTime(LocalDateTime.now());

        // 4. 保存评价
        commentMapper.insert(comment);

        // 5. 更新商品评分和评价数量
        updateProductCommentStats(orderItem.getProductId());

        return comment.getId();
    }

    /**
     * 查询客户自身评价列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @param params   查询参数
     * @return 评价列表（分页）
     */
    @Override
    public Page<CommentVO> getMyComments(Long userId, Integer pageNum, Integer pageSize, Map<String, Object> params) {
        // 1. 设置分页参数
        Page<CommentVO> page = new Page<>(pageNum, pageSize);

        // 2. 设置查询参数
        params.put("userId", userId);

        // 3. 查询评价列表
        Page<CommentVO> commentVOPage = commentMapper.selectByUserId(page, params);

        // 4. 处理评价图片（字符串转列表）
        processCommentImages(commentVOPage.getRecords());

        return commentVOPage;
    }

    /**
     * 查询商家商品评价列表
     *
     * @param shopId    商家关联的商铺ID
     * @param pageNum   页码
     * @param pageSize  每页条数
     * @param params    查询参数
     * @return 评价列表（分页）
     */
    @Override
    public Page<CommentVO> getMerchantComments(Long shopId, Integer pageNum, Integer pageSize, Map<String, Object> params) {
        // 1. 设置分页参数
        Page<CommentVO> page = new Page<>(pageNum, pageSize);

        // 2. 设置查询参数
        params.put("shopId", shopId);

        // 3. 查询评价列表
        Page<CommentVO> commentVOPage = commentMapper.selectByShopId(page, params);

        // 4. 处理评价图片（字符串转列表）
        processCommentImages(commentVOPage.getRecords());

        // 5. 客户昵称脱敏
        processUserNickname(commentVOPage.getRecords());

        return commentVOPage;
    }

    /**
     * 查询商品评价列表（公共）
     *
     * @param productId 商品ID
     * @param pageNum   页码
     * @param pageSize  每页条数
     * @return 评价列表（分页）
     */
    @Override
    public Page<CommentVO> getProductComments(Long productId, Integer pageNum, Integer pageSize) {
        // 1. 设置分页参数
        Page<CommentVO> page = new Page<>(pageNum, pageSize);

        // 2. 查询评价列表
        Page<CommentVO> commentVOPage = commentMapper.selectByProductId(page, productId);

        // 3. 处理评价图片（字符串转列表）
        processCommentImages(commentVOPage.getRecords());

        // 4. 客户昵称脱敏
        processUserNickname(commentVOPage.getRecords());

        return commentVOPage;
    }

    /**
     * 查询商品评价统计信息
     *
     * @param productId 商品ID
     * @return 统计信息，包含平均评分和评价数量
     */
    @Override
    public Map<String, Object> getProductCommentStats(Long productId) {
        return commentMapper.selectProductCommentStats(productId);
    }

    /**
     * 检查订单详情是否可评价
     *
     * @param userId     用户ID
     * @param orderItemId 订单详情ID
     * @return 可评价返回true，否则返回false
     */
    @Override
    public boolean checkCommentAble(Long userId, Long orderItemId) {
        // 1. 查询订单详情
        OrderItem orderItem = orderItemMapper.selectById(orderItemId);
        if (orderItem == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单详情不存在");
        }

        // 2. 查询订单
        Order order = orderMapper.selectById(orderItem.getOrderId());
        if (order == null) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单不存在");
        }

        // 3. 校验订单是否归属当前客户
        if (!order.getUserId().equals(userId)) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单不归属当前客户");
        }

        // 4. 校验订单状态是否为交易完成（5）
        if (order.getStatus() != 5) {
            throw new GlobalExceptionHandler.BusinessException(400, "订单未完成，无法评价");
        }

        // 5. 校验该订单详情是否已发布过评价
        Comment comment = commentMapper.selectByOrderItemId(orderItemId);
        if (comment != null) {
            throw new GlobalExceptionHandler.BusinessException(400, "该商品已评价");
        }

        return true;
    }

    /**
     * 将评价图片列表转换为逗号分隔的字符串
     *
     * @param images 评价图片URL列表
     * @return 逗号分隔的图片URL字符串
     */
    @Override
    public String convertImagesToString(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return String.join(",", images);
    }

    /**
     * 将逗号分隔的图片URL字符串转换为列表
     *
     * @param imagesStr 逗号分隔的图片URL字符串
     * @return 评价图片URL列表
     */
    @Override
    public List<String> convertStringToImages(String imagesStr) {
        if (imagesStr == null || imagesStr.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(imagesStr.split(","));
    }

    /**
     * 客户昵称脱敏处理
     *
     * @param nickname 客户昵称
     * @return 脱敏后的昵称
     */
    @Override
    public String desensitizeNickname(String nickname) {
        if (nickname == null || nickname.length() <= 1) {
            return nickname;
        }
        if (nickname.length() == 2) {
            return nickname.charAt(0) + "*";
        }
        if (nickname.length() == 3) {
            return nickname.charAt(0) + "*" + nickname.charAt(2);
        }
        // 超过3个字符，中间部分用*替换
        int length = nickname.length();
        int middleLength = length - 2;
        StringBuilder sb = new StringBuilder();
        sb.append(nickname.charAt(0));
        for (int i = 0; i < middleLength; i++) {
            sb.append("*");
        }
        sb.append(nickname.charAt(length - 1));
        return sb.toString();
    }

    /**
     * 更新商品评分和评价数量
     *
     * @param productId 商品ID
     */
    private void updateProductCommentStats(Long productId) {
        // 1. 查询商品评价统计信息
        Map<String, Object> stats = commentMapper.selectProductCommentStats(productId);
        if (stats == null) {
            return;
        }

        // 2. 获取平均评分和评价数量
        BigDecimal averageScore = (BigDecimal) stats.get("averageScore");
        Integer commentCount = (Integer) stats.get("commentCount");

        // 3. 更新商品评分和评价数量
        productMapper.updateCommentStats(productId, averageScore, commentCount);
    }

    /**
     * 处理评价图片（字符串转列表）
     *
     * @param commentVOs 评价VO列表
     */
    private void processCommentImages(List<CommentVO> commentVOs) {
        if (commentVOs == null || commentVOs.isEmpty()) {
            return;
        }
        for (CommentVO commentVO : commentVOs) {
            // 查询评价信息获取images字符串
            Comment comment = commentMapper.selectById(commentVO.getId());
            if (comment != null) {
                commentVO.setImages(convertStringToImages(comment.getImages()));
            }
        }
    }

    /**
     * 处理客户昵称脱敏
     *
     * @param commentVOs 评价VO列表
     */
    private void processUserNickname(List<CommentVO> commentVOs) {
        if (commentVOs == null || commentVOs.isEmpty()) {
            return;
        }
        for (CommentVO commentVO : commentVOs) {
            if (commentVO.getUserNickname() != null) {
                commentVO.setUserNickname(desensitizeNickname(commentVO.getUserNickname()));
            }
        }
    }

}