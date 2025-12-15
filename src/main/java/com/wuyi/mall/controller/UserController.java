package com.wuyi.mall.controller;

import com.wuyi.mall.service.UserService;
import com.wuyi.mall.util.ResultUtil;
import com.wuyi.mall.vo.LoginVO;
import com.wuyi.mall.vo.RegisterVO;
import com.wuyi.mall.vo.UserLoginVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 *
 * @author wuyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * 用户服务
     */
    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     *
     * @param registerVO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResultUtil.Result<Long> register(@Valid @RequestBody RegisterVO registerVO) {
        // 调用用户服务进行注册
        Long userId = userService.register(registerVO);
        // 返回注册成功结果
        return ResultUtil.success(userId);
    }

    /**
     * 用户登录接口
     *
     * @param loginVO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResultUtil.Result<UserLoginVO> login(@Valid @RequestBody LoginVO loginVO) {
        // 调用用户服务进行登录
        UserLoginVO userLoginVO = userService.login(loginVO);
        // 返回登录成功结果
        return ResultUtil.success(userLoginVO);
    }

}