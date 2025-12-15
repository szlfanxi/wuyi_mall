package com.wuyimall.config;

import com.wuyimall.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 获取请求头中的 Authorization 字段
        String authHeader = request.getHeader("Authorization");
        
        // 2. 如果请求头中没有 Authorization 或者不是 Bearer 开头，直接放行（登录和注册接口不需要认证）
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 3. 解析 Token
        String token = authHeader.substring(7); // 去掉 "Bearer " 前缀
        Long userId = null;
        
        try {
            // 4. 验证 Token 并获取 userId
            userId = jwtUtil.getUserId(token);
        } catch (JwtException e) {
            // 5. Token 无效，返回 401 错误
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"无效的 Token\"}");
            return;
        }
        
        // 6. 将 userId 注入到请求中，便于后续 Controller 中获取
        request.setAttribute("userId", userId);
        
        // 7. 放行请求
        filterChain.doFilter(request, response);
    }
}