package com.wuyimall.service;

import com.wuyimall.dto.LoginRequest;
import com.wuyimall.dto.RegisterRequest;
import com.wuyimall.entity.User;
import com.wuyimall.repository.UserRepository;
import com.wuyimall.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
    /**
     * 用户注册
     * @param request 注册请求参数
     * @throws AuthException 注册失败时抛出
     */
    // 注册
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setNickname(request.getNickname());

        userRepository.save(user);
    }

    // 登录
    public Map<String, Object> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 登录成功 → 发 token
        String token = jwtUtil.generateToken(user.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("isAdmin", user.getIsAdmin());
        return map;
    }
    
    // 获取用户信息
    public User getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }
}
