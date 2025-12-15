package com.wuyimall.controller;

import com.wuyimall.dto.CategoryDTO;
import com.wuyimall.entity.Category;
import com.wuyimall.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.wuyimall.service.AdminService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员分类管理控制器
 */
@RestController
@RequestMapping("/api/admin/categories")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AdminCategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 获取当前登录用户ID
     * @param request HTTP请求
     * @return 用户ID
     */
    private Long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        return userId;
    }
    
    /**
     * 获取分类树形结构（用于管理界面）
     * @param request HTTP请求
     * @return 分类树形结构
     */
    @GetMapping("/tree")
    public List<CategoryDTO> getCategoryTree(HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);
        return categoryService.getCategoryTree();
    }
    
    /**
     * 获取所有分类（平铺结构，用于下拉选择）
     * @param request HTTP请求
     * @return 分类列表
     */
    @GetMapping("/all")
    public List<Category> getAllCategories(HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);
        return categoryService.getAllCategories();
    }
    
    /**
     * 获取一级分类列表
     * @param request HTTP请求
     * @return 一级分类列表
     */
    @GetMapping("/first-level")
    public List<Category> getFirstLevelCategories(HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);
        return categoryService.getFirstLevelCategories();
    }
    
    /**
     * 根据父分类ID获取子分类列表
     * @param parentId 父分类ID
     * @param request HTTP请求
     * @return 子分类列表
     */
    @GetMapping("/children/{parentId}")
    public List<Category> getChildCategories(@PathVariable Long parentId, HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);
        return categoryService.getChildCategories(parentId);
    }
    
    /**
     * 根据ID获取分类详情
     * @param id 分类ID
     * @param request HTTP请求
     * @return 分类详情
     */
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);
        return categoryService.getCategoryById(id);
    }
    
    /**
     * 创建分类
     * @param category 分类信息
     * @param request HTTP请求
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody Category category, HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            Category createdCategory = categoryService.createCategory(category);
            response.put("success", true);
            response.put("data", createdCategory);
            response.put("message", "分类创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 更新分类
     * @param id 分类ID
     * @param category 分类信息
     * @param request HTTP请求
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(@PathVariable Long id, @RequestBody Category category, HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            category.setId(id);
            Category updatedCategory = categoryService.updateCategory(category);
            response.put("success", true);
            response.put("data", updatedCategory);
            response.put("message", "分类更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 删除分类
     * @param id 分类ID
     * @param request HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);
        
        Map<String, Object> response = new HashMap<>();
        try {
            categoryService.deleteCategory(id);
            response.put("success", true);
            response.put("message", "分类删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}