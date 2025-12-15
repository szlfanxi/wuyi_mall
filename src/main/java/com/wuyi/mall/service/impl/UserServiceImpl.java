package com.wuyi.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyi.mall.entity.User;
import com.wuyi.mall.handler.GlobalExceptionHandler;
import com.wuyi.mall.mapper.UserMapper;
import com.wuyi.mall.service.UserService;
import com.wuyi.mall.util.JwtUtil;
import com.wuyi.mall.vo.LoginVO;
import com.wuyi.mall.vo.RegisterVO;
import com.wuyi.mall.vo.UserLoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 用户Mapper
     */
    @Autowired
    private UserMapper userMapper;

    /**
     * BCrypt密码加密器
     */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * JWT工具类
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     *
     * @param registerVO 注册信息
     * @return 注册成功的用户ID
     */
    @Override
    public Long register(RegisterVO registerVO) {
        // 1. 校验用户名是否已存在
        User existingUserByUsername = userMapper.selectByUsername(registerVO.getUsername());
        if (existingUserByUsername != null) {
            throw new GlobalExceptionHandler.BusinessException(400, "用户名已存在");
        }

        // 2. 校验手机号是否已存在
        User existingUserByPhone = userMapper.selectByPhone(registerVO.getPhone());
        if (existingUserByPhone != null) {
            throw new GlobalExceptionHandler.BusinessException(400, "手机号已存在");
        }

        // 3. 创建用户对象
        User user = new User();
        BeanUtils.copyProperties(registerVO, user);

        // 4. 加密密码
        String encodedPassword = passwordEncoder.encode(registerVO.getPassword());
        user.setPassword(encodedPassword);

        // 5. 设置默认角色
        user.setRole("客户");

        // 6. 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        // 7. 设置删除标记为0（未删除）
        user.setDeleted(0);

        // 8. 保存用户到数据库
        int insertResult = userMapper.insert(user);
        if (insertResult <= 0) {
            throw new GlobalExceptionHandler.BusinessException(500, "注册失败，请重试");
        }

        // 9. 返回用户ID
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param loginVO 登录信息
     * @return 登录成功的用户信息和JWT令牌
     */
    @Override
    public UserLoginVO login(LoginVO loginVO) {
        // 1. 根据用户名或手机号查询用户
        User user = userMapper.selectByUsernameOrPhone(loginVO.getAccount());
        if (user == null) {
            throw new GlobalExceptionHandler.BusinessException(401, "账号或密码错误");
        }

        // 2. 校验密码
        boolean passwordMatch = passwordEncoder.matches(loginVO.getPassword(), user.getPassword());
        if (!passwordMatch) {
            throw new GlobalExceptionHandler.BusinessException(401, "账号或密码错误");
        }

        // 3. 生成JWT令牌
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        // 4. 构建登录成功返回对象
        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setId(user.getId());
        userLoginVO.setUsername(user.getUsername());
        userLoginVO.setPhone(user.getPhone());
        userLoginVO.setRole(user.getRole());
        userLoginVO.setToken(token);

        // 5. 返回登录成功信息
        return userLoginVO;
    }

    /**
     * 根据用户名或手机号查询用户
     *
     * @param account 用户名或手机号
     * @return 用户信息
     */
    @Override
    public User getByUsernameOrPhone(String account) {
        return userMapper.selectByUsernameOrPhone(account);
    }

}