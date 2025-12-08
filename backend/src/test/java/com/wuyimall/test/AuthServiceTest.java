package com.wuyimall.test;

import com.wuyimall.dto.RegisterRequest;
import com.wuyimall.service.AuthService;
import com.wuyimall.repository.UserRepository;
import com.wuyimall.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void register_shouldCreateUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        String username = "test_" + UUID.randomUUID();
        request.setUsername(username);
        request.setPassword("password123");
        request.setNickname("test");

        authService.register(request);

        // 清理测试数据，避免污染真实库
        User created = userRepository.findByUsername(username);
        if (created != null) {
            userRepository.delete(created);
        }
    }
}
