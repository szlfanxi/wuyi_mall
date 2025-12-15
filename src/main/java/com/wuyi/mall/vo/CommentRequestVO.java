package com.wuyi.mall.vo;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * 发布评价请求VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class CommentRequestVO {

    /**
     * 订单详情ID
     */
    @NotNull(message = "订单详情ID不能为空")
    private Long orderItemId;

    /**
     * 评价内容（1-500字）
     */
    @NotBlank(message = "评价内容不能为空")
    @Size(min = 1, max = 500, message = "评价内容长度必须在1-500字之间")
    private String content;

    /**
     * 评价图片URL列表（最多5张）
     */
    @Size(max = 5, message = "评价图片最多5张")
    private List<String> images;

    /**
     * 评分（1-5分）
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分必须大于等于1分")
    @Max(value = 5, message = "评分必须小于等于5分")
    private Integer score;

}