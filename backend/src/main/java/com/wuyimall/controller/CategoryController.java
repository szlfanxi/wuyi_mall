package com.wuyimall.controller;

import com.wuyimall.dto.CategoryDTO;
import com.wuyimall.entity.Category;
import com.wuyimall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类列表（平铺结构）
     * @return 分类列表
     */
    @GetMapping
    public List<Category> list() {
        return categoryService.getAllCategories();
    }

    /**
     * 获取分类树形结构（二级分类）
     * @return 分类树形结构
     */
    @GetMapping("/tree")
    public List<CategoryDTO> getCategoryTree() {
        return categoryService.getCategoryTree();
    }

    /**
     * 获取一级分类列表
     * @return 一级分类列表
     */
    @GetMapping("/first-level")
    public List<Category> getFirstLevelCategories() {
        return categoryService.getFirstLevelCategories();
    }

    /**
     * 根据父分类ID获取子分类列表
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @GetMapping("/children/{parentId}")
    public List<Category> getChildCategories(@PathVariable Long parentId) {
        return categoryService.getChildCategories(parentId);
    }

    /**
     * 根据ID获取分类详情
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/{id}")
    public Category detail(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }
    
    /**
     * 创建分类
     * @param category 分类信息
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Category category) {
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
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody Category category) {
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
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
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