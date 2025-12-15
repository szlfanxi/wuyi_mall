package com.wuyi.mall.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价返回VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class CommentVO {

    /**
     * 评价ID
     */
    private Long id;

    /**
     * 订单详情ID
     */
    private Long orderItemId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 客户昵称（脱敏）
     */
    private String userNickname;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评分，1-5分
     */
    private Integer score;

    /**
     * 评价图片URL列表
     */
    private List<String> images;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}