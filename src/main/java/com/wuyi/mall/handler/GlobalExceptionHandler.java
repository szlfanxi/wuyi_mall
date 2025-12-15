package com.wuyi.mall.handler;

import com.wuyi.mall.util.ResultUtil;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局异常处理器
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常（@Validated + @RequestBody）
     *
     * @param e MethodArgumentNotValidException
     * @return 统一响应对象
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultUtil.Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 获取所有校验错误信息
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        // 拼接错误信息
        StringBuilder errorMsg = new StringBuilder();
        for (ObjectError error : errors) {
            errorMsg.append(error.getDefaultMessage()).append(", ");
        }
        // 去除最后一个逗号和空格
        String msg = errorMsg.length() > 0 ? errorMsg.substring(0, errorMsg.length() - 2) : "参数校验失败";
        // 返回参数错误响应
        return ResultUtil.paramError(msg);
    }

    /**
     * 处理参数绑定异常（@Validated + @RequestParam/@PathVariable）
     *
     * @param e BindException
     * @return 统一响应对象
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultUtil.Result<Object> handleBindException(BindException e) {
        // 获取所有校验错误信息
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        // 拼接错误信息
        StringBuilder errorMsg = new StringBuilder();
        for (ObjectError error : errors) {
            errorMsg.append(error.getDefaultMessage()).append(", ");
        }
        // 去除最后一个逗号和空格
        String msg = errorMsg.length() > 0 ? errorMsg.substring(0, errorMsg.length() - 2) : "参数绑定失败";
        // 返回参数错误响应
        return ResultUtil.paramError(msg);
    }

    /**
     * 处理约束违反异常（@Validated + 方法参数）
     *
     * @param e ConstraintViolationException
     * @return 统一响应对象
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultUtil.Result<Object> handleConstraintViolationException(ConstraintViolationException e) {
        // 拼接错误信息
        StringBuilder errorMsg = new StringBuilder();
        e.getConstraintViolations().forEach(violation -> {
            errorMsg.append(violation.getMessage()).append(", ");
        });
        // 去除最后一个逗号和空格
        String msg = errorMsg.length() > 0 ? errorMsg.substring(0, errorMsg.length() - 2) : "约束违反失败";
        // 返回参数错误响应
        return ResultUtil.paramError(msg);
    }

    /**
     * 处理业务异常
     *
     * @param e BusinessException
     * @return 统一响应对象
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultUtil.Result<Object> handleBusinessException(BusinessException e) {
        // 返回业务错误响应
        return ResultUtil.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理其他异常
     *
     * @param e Exception
     * @return 统一响应对象
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultUtil.Result<Object> handleException(Exception e) {
        // 打印异常信息
        e.printStackTrace();
        // 返回系统错误响应
        return ResultUtil.fail(500, "系统内部错误");
    }

    /**
     * 业务异常类
     *
     * @author wuyi
     * @date 2025-12-04
     */
    public static class BusinessException extends RuntimeException {

        /**
         * 错误码
         */
        private int code;

        /**
         * 错误信息
         */
        private String message;

        /**
         * 构造方法
         *
         * @param code    错误码
         * @param message 错误信息
         */
        public BusinessException(int code, String message) {
            super(message);
            this.code = code;
            this.message = message;
        }

        /**
         * 构造方法
         *
         * @param message 错误信息
         */
        public BusinessException(String message) {
            this(500, message);
        }

        /**
         * 获取错误码
         *
         * @return 错误码
         */
        public int getCode() {
            return code;
        }

        /**
         * 设置错误码
         *
         * @param code 错误码
         */
        public void setCode(int code) {
            this.code = code;
        }

        /**
         * 获取错误信息
         *
         * @return 错误信息
         */
        @Override
        public String getMessage() {
            return message;
        }

        /**
         * 设置错误信息
         *
         * @param message 错误信息
         */
        public void setMessage(String message) {
            this.message = message;
        }
    }

}