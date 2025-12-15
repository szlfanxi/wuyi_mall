package com.wuyi.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.Comment;
import com.wuyi.mall.vo.CommentRequestVO;
import com.wuyi.mall.vo.CommentVO;

import java.util.List;
import java.util.Map;

/**
 * 评价服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface CommentService {

    /**
     * 发布商品评价
     *
     * @param userId           用户ID
     * @param commentRequestVO 评价请求参数
     * @return 评价ID
     */
    Long publishComment(Long userId, CommentRequestVO commentRequestVO);

    /**
     * 查询客户自身评价列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @param params   查询参数
     * @return 评价列表（分页）
     */
    Page<CommentVO> getMyComments(Long userId, Integer pageNum, Integer pageSize, Map<String, Object> params);

    /**
     * 查询商家商品评价列表
     *
     * @param shopId    商家关联的商铺ID
     * @param pageNum   页码
     * @param pageSize  每页条数
     * @param params    查询参数
     * @return 评价列表（分页）
     */
    Page<CommentVO> getMerchantComments(Long shopId, Integer pageNum, Integer pageSize, Map<String, Object> params);

    /**
     * 查询商品评价列表（公共）
     *
     * @param productId 商品ID
     * @param pageNum   页码
     * @param pageSize  每页条数
     * @return 评价列表（分页）
     */
    Page<CommentVO> getProductComments(Long productId, Integer pageNum, Integer pageSize);

    /**
     * 查询商品评价统计信息
     *
     * @param productId 商品ID
     * @return 统计信息，包含平均评分和评价数量
     */
    Map<String, Object> getProductCommentStats(Long productId);

    /**
     * 检查订单详情是否可评价
     *
     * @param userId     用户ID
     * @param orderItemId 订单详情ID
     * @return 可评价返回true，否则返回false
     */
    boolean checkCommentAble(Long userId, Long orderItemId);

    /**
     * 将评价图片列表转换为逗号分隔的字符串
     *
     * @param images 评价图片URL列表
     * @return 逗号分隔的图片URL字符串
     */
    String convertImagesToString(List<String> images);

    /**
     * 将逗号分隔的图片URL字符串转换为列表
     *
     * @param imagesStr 逗号分隔的图片URL字符串
     * @return 评价图片URL列表
     */
    List<String> convertStringToImages(String imagesStr);

    /**
     * 客户昵称脱敏处理
     *
     * @param nickname 客户昵称
     * @return 脱敏后的昵称
     */
    String desensitizeNickname(String nickname);

}