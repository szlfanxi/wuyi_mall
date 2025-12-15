package com.wuyi.mall.util;

import lombok.Data;

import java.util.Date;

/**
 * 统一响应工具类
 *
 * @author wuyi
 * @date 2025-12-04
 */
public class ResultUtil {

    /**
     * 成功响应，带业务数据
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        result.setTimestamp(new Date());
        return result;
    }

    /**
     * 成功响应，带业务数据和自定义消息
     *
     * @param data 业务数据
     * @param msg  自定义消息
     * @param <T>  数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> success(T data, String msg) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        result.setTimestamp(new Date());
        return result;
    }

    /**
     * 成功响应，不带业务数据
     *
     * @param <T> 数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 失败响应
     *
     * @param code 状态码
     * @param msg  提示信息
     * @param <T>  数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> fail(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        result.setTimestamp(new Date());
        return result;
    }

    /**
     * 失败响应，默认状态码500
     *
     * @param msg 提示信息
     * @param <T> 数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> fail(String msg) {
        return fail(500, msg);
    }

    /**
     * 参数错误响应，默认状态码400
     *
     * @param msg 提示信息
     * @param <T> 数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> paramError(String msg) {
        return fail(400, msg);
    }

    /**
     * 认证失败响应，默认状态码401
     *
     * @param msg 提示信息
     * @param <T> 数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> authError(String msg) {
        return fail(401, msg);
    }

    /**
     * 统一响应对象
     *
     * @param <T> 业务数据类型
     */
    @Data
    public static class Result<T> {

        /**
         * 状态码
         */
        private int code;

        /**
         * 提示信息
         */
        private String msg;

        /**
         * 业务数据
         */
        private T data;

        /**
         * 时间戳
         */
        private Date timestamp;

    }

}