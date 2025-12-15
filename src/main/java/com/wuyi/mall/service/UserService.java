package com.wuyi.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyi.mall.entity.User;
import com.wuyi.mall.vo.LoginVO;
import com.wuyi.mall.vo.RegisterVO;
import com.wuyi.mall.vo.UserLoginVO;

/**
 * 用户服务接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param registerVO 注册信息
     * @return 注册成功的用户ID
     */
    Long register(RegisterVO registerVO);

    /**
     * 用户登录
     *
     * @param loginVO 登录信息
     * @return 登录成功的用户信息和JWT令牌
     */
    UserLoginVO login(LoginVO loginVO);

    /**
     * 根据用户名或手机号查询用户
     *
     * @param account 用户名或手机号
     * @return 用户信息
     */
    User getByUsernameOrPhone(String account);

}