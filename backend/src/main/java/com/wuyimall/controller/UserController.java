package com.wuyimall.controller;

import com.wuyimall.dto.LoginRequest;
import com.wuyimall.dto.RegisterRequest;
import com.wuyimall.entity.User;
import com.wuyimall.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}) 
public class UserController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册接口
     * @param request 注册请求参数
     * @return 注册结果
     */
    @PostMapping("/users/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            authService.register(request);
            response.put("success", true);
            response.put("message", "注册成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 用户登录接口
     * @param request 登录请求参数
     * @return 登录结果和JWT令牌
     */
    @PostMapping("/users/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> loginResult = authService.login(request);
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("token", loginResult.get("token"));
            response.put("isAdmin", loginResult.get("isAdmin"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取用户信息接口
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/user/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                throw new RuntimeException("用户未登录");
            }
            
            User user = authService.getUserInfo(userId);
            response.put("success", true);
            response.put("username", user.getUsername());
            response.put("nickname", user.getNickname());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}