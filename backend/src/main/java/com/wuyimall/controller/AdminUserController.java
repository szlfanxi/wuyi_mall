package com.wuyimall.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import com.wuyimall.service.AdminService;
import com.wuyimall.repository.UserRepository;
import com.wuyimall.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AdminUserController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    private Long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        return userId;
    }

    // 用户列表
    @GetMapping("/list")
    public List<User> list(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);

        // 获取所有用户
        List<User> allUsers = userRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 筛选和搜索
        return allUsers.stream()
                // 按用户名搜索
                .filter(user -> username == null || user.getUsername().toLowerCase().contains(username.toLowerCase()))
                // 按角色筛选
                .filter(user -> role == null || role.isEmpty() || 
                        (role.equals("admin") && user.getIsAdmin() == 1) || 
                        (role.equals("user") && (user.getIsAdmin() == 0 || user.getIsAdmin() == null)))
                // 按状态筛选
                .filter(user -> status == null || status.isEmpty() || 
                        (status.equals("active") && (user.getStatus() == 0 || user.getStatus() == null)) || 
                        (status.equals("inactive") && user.getStatus() == 1))
                .collect(Collectors.toList());
    }

    // 获取单个用户
    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId, HttpServletRequest request) {
        Long currentUserId = getUserId(request);
        adminService.checkAdmin(currentUserId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    // 更新用户状态
    @PostMapping("/changeStatus")
    public String changeStatus(@RequestParam Long userId,
                              @RequestParam Integer status,
                              HttpServletRequest request) {
        Long currentUserId = getUserId(request);
        adminService.checkAdmin(currentUserId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setStatus(status);
        userRepository.save(user);
        return "用户状态更新成功";
    }

    // 删除用户
    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long userId, HttpServletRequest request) {
        Long currentUserId = getUserId(request);
        adminService.checkAdmin(currentUserId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 不能删除自己
        if (user.getId().equals(currentUserId)) {
            throw new RuntimeException("不能删除自己");
        }

        userRepository.delete(user);
        return "用户删除成功";
    }
}