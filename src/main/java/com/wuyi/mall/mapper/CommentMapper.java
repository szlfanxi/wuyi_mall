package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.entity.Comment;
import com.wuyi.mall.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 评价Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 根据订单详情ID查询评价
     *
     * @param orderItemId 订单详情ID
     * @return 评价信息
     */
    Comment selectByOrderItemId(@Param("orderItemId") Long orderItemId);

    /**
     * 查询客户自身评价列表（分页）
     *
     * @param page 分页参数
     * @param params 查询参数，包含userId、productId、createTimeStart、createTimeEnd
     * @return 评价列表
     */
    Page<CommentVO> selectByUserId(Page<CommentVO> page, @Param("params") Map<String, Object> params);

    /**
     * 查询商家商品评价列表（分页）
     *
     * @param page 分页参数
     * @param params 查询参数，包含shopId、productId、score、createTimeStart、createTimeEnd
     * @return 评价列表
     */
    Page<CommentVO> selectByShopId(Page<CommentVO> page, @Param("params") Map<String, Object> params);

    /**
     * 查询商品评价列表（公共，分页）
     *
     * @param page 分页参数
     * @param productId 商品ID
     * @return 评价列表
     */
    Page<CommentVO> selectByProductId(Page<CommentVO> page, @Param("productId") Long productId);

    /**
     * 查询商品评价统计信息
     *
     * @param productId 商品ID
     * @return 统计信息，包含平均评分和评价数量
     */
    Map<String, Object> selectProductCommentStats(@Param("productId") Long productId);

}