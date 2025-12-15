package com.wuyi.mall.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author wuyi
 * @date 2025-12-04
 */
@Component
public class JwtUtil {

    /**
     * JWT密钥，从配置文件中读取
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT有效期，单位：毫秒，从配置文件中读取
     */
    @Value("${jwt.expire}")
    private Long expire;

    /**
     * 生成JWT令牌
     *
     * @param userId 用户ID
     * @param role   用户角色
     * @return JWT令牌
     */
    public String generateToken(Long userId, String role) {
        // 设置JWT声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        // 生成签名密钥
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // 生成JWT令牌
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 校验JWT令牌
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            // 生成签名密钥
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            // 解析JWT令牌
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // 令牌无效
            return false;
        }
    }

    /**
     * 从JWT令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            // 生成签名密钥
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            // 解析JWT令牌
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            Claims claims = claimsJws.getPayload();

            return Long.parseLong(claims.get("userId").toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从JWT令牌中获取用户角色
     *
     * @param token JWT令牌
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        try {
            // 生成签名密钥
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            // 解析JWT令牌
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            Claims claims = claimsJws.getPayload();

            return claims.get("role").toString();
        } catch (Exception e) {
            return null;
        }
    }

}