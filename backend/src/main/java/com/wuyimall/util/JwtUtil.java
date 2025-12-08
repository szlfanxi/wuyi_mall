package com.wuyimall.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // 足够长度的密钥，至少 256 位（32 个字符）
    private final String secret = "mall-secret-key-2025-12-06-secure-jwt-key-for-wuyi-mall-app";
    private final long expire = 1000 * 60 * 60 * 24; // 24小时

    // 生成密钥
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 生成 Token
    public String generateToken(Long userId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expire);

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSigningKey())
                .compact();
    }

    // 从 Token 中获取 userId
    public Long getUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token 已过期");
        } catch (JwtException e) {
            throw new RuntimeException("Token 无效");
        }
    }
}