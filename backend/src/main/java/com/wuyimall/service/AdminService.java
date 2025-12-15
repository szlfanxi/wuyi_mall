package com.wuyimall.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.wuyimall.entity.User;
import com.wuyimall.repository.UserRepository;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    public void checkAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (user.getIsAdmin() == null || user.getIsAdmin() == 0) {
            throw new RuntimeException("无管理员权限");
        }
    }
}