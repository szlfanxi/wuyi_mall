package com.wuyimall.controller;

import com.wuyimall.entity.Category;
import com.wuyimall.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * 获取分类列表
     * @return 分类列表
     */
    @GetMapping
    public List<Category> list() {
        return categoryRepository.findAll();
    }

    /**
     * 根据ID获取分类详情
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/{id}")
    public Category detail(@PathVariable Long id) {
        return categoryRepository.findById(id).orElse(null);
    }
}