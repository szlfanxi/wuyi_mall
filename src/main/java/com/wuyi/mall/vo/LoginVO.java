package com.wuyi.mall.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class LoginVO {

    /**
     * 登录账号，支持用户名/手机号
     */
    @NotBlank(message = "登录账号不能为空")
    private String account;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

}