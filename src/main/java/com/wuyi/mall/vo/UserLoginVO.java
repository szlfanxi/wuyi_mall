package com.wuyi.mall.vo;

import lombok.Data;

/**
 * 登录成功返回VO
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Data
public class UserLoginVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户角色
     */
    private String role;

    /**
     * JWT令牌
     */
    private String token;

}