package com.wuyimall.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCrypt;
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

    // 更新用户信息（角色和状态）
    @PostMapping("/update")
    public String updateUser(@RequestBody UserUpdateRequest request,
                            HttpServletRequest servletRequest) {
        Long currentUserId = getUserId(servletRequest);
        adminService.checkAdmin(currentUserId);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新用户信息
        user.setIsAdmin(request.getIsAdmin());
        user.setStatus(request.getStatus());
        userRepository.save(user);
        return "用户信息更新成功";
    }
    
    // 添加用户
    @PostMapping("/add")
    public String addUser(@RequestBody UserAddRequest request,
                         HttpServletRequest servletRequest) {
        Long currentUserId = getUserId(servletRequest);
        adminService.checkAdmin(currentUserId);
        
        // 检查用户名是否已存在
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        // 密码加密
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setNickname(request.getUsername()); // 默认使用用户名作为昵称
        user.setIsAdmin(request.getIsAdmin());
        user.setStatus(request.getStatus());
        
        userRepository.save(user);
        return "用户添加成功";
    }
    
    // 内部类，用于接收更新用户请求
    private static class UserUpdateRequest {
        private Long userId;
        private Integer isAdmin;
        private Integer status;
        
        // Getters and Setters
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public Integer getIsAdmin() {
            return isAdmin;
        }
        
        public void setIsAdmin(Integer isAdmin) {
            this.isAdmin = isAdmin;
        }
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
    }
    
    // 内部类，用于接收添加用户请求
    private static class UserAddRequest {
        private String username;
        private String password;
        private Integer isAdmin;
        private Integer status;
        
        // Getters and Setters
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public Integer getIsAdmin() {
            return isAdmin;
        }
        
        public void setIsAdmin(Integer isAdmin) {
            this.isAdmin = isAdmin;
        }
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}