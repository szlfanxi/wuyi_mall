package com.wuyi.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyi.mall.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名或手机号查询用户
     *
     * @param account 用户名或手机号
     * @return 用户信息
     */
    User selectByUsernameOrPhone(@Param("account") String account);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User selectByPhone(@Param("phone") String phone);

}