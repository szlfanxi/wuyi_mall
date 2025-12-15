package com.wuyi.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuyi.mall.service.CommentService;
import com.wuyi.mall.util.ResultUtil;
import com.wuyi.mall.vo.CommentRequestVO;
import com.wuyi.mall.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 评价控制器
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    /**
     * 评价服务
     */
    @Autowired
    private CommentService commentService;

    /**
     * 发布商品评价
     *
     * @param authentication 认证信息
     * @param commentRequestVO 评价请求参数
     * @return 评价ID
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResultUtil.Result<Map<String, Long>> publishComment(Authentication authentication, @Valid @RequestBody CommentRequestVO commentRequestVO) {
        // 从认证信息中获取用户ID
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 调用评价服务发布评价
        Long commentId = commentService.publishComment(userId, commentRequestVO);

        // 构建返回结果
        Map<String, Long> data = new HashMap<>();
        data.put("commentId", commentId);

        return ResultUtil.success(data);
    }

    /**
     * 查询客户自身评价列表
     *
     * @param authentication 认证信息
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param productId 商品ID（可选）
     * @param createTimeStart 创建时间开始（可选）
     * @param createTimeEnd 创建时间结束（可选）
     * @return 评价列表（分页）
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResultUtil.Result<Page<CommentVO>> getMyComments(Authentication authentication,
                                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                                       @RequestParam(required = false) Long productId,
                                                       @RequestParam(required = false) String createTimeStart,
                                                       @RequestParam(required = false) String createTimeEnd) {
        // 从认证信息中获取用户ID
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("createTimeStart", createTimeStart);
        params.put("createTimeEnd", createTimeEnd);

        // 调用评价服务查询评价列表
        Page<CommentVO> commentVOPage = commentService.getMyComments(userId, pageNum, pageSize, params);

        return ResultUtil.success(commentVOPage);
    }

    /**
     * 查询商家商品评价列表
     *
     * @param authentication 认证信息
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @param productId 商品ID（可选）
     * @param score 评分（可选）
     * @param createTimeStart 创建时间开始（可选）
     * @param createTimeEnd 创建时间结束（可选）
     * @return 评价列表（分页）
     */
    @GetMapping("/merchant")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResultUtil.Result<Page<CommentVO>> getMerchantComments(Authentication authentication,
                                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                                              @RequestParam(required = false) Long productId,
                                                              @RequestParam(required = false) Integer score,
                                                              @RequestParam(required = false) String createTimeStart,
                                                              @RequestParam(required = false) String createTimeEnd) {
        // 从认证信息中获取商家ID（这里假设商家ID就是用户ID，实际项目中可能需要关联shop表）
        Long shopId = Long.parseLong(authentication.getPrincipal().toString());

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("productId", productId);
        params.put("score", score);
        params.put("createTimeStart", createTimeStart);
        params.put("createTimeEnd", createTimeEnd);

        // 调用评价服务查询评价列表
        Page<CommentVO> commentVOPage = commentService.getMerchantComments(shopId, pageNum, pageSize, params);

        return ResultUtil.success(commentVOPage);
    }

    /**
     * 查询商品评价列表（公共）
     *
     * @param productId 商品ID
     * @param pageNum 页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 评价列表（分页）+ 商品评价统计信息
     */
    @GetMapping("/product/{productId}")
    public ResultUtil.Result<Map<String, Object>> getProductComments(@PathVariable Long productId,
                                                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        // 调用评价服务查询评价列表
        Page<CommentVO> commentVOPage = commentService.getProductComments(productId, pageNum, pageSize);

        // 查询商品评价统计信息
        Map<String, Object> stats = commentService.getProductCommentStats(productId);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", commentVOPage.getRecords());
        result.put("averageScore", stats.get("averageScore"));
        result.put("totalCount", stats.get("commentCount"));
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("total", commentVOPage.getTotal());
        result.put("pages", commentVOPage.getPages());

        return ResultUtil.success(result);
    }

}