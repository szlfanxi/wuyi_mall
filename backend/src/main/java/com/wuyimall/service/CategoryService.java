package com.wuyimall.service;

import com.wuyimall.dto.CategoryDTO;
import com.wuyimall.entity.Category;
import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {
    
    /**
     * 获取所有分类（平铺结构）
     * @return 分类列表
     */
    List<Category> getAllCategories();
    
    /**
     * 获取分类树形结构（二级分类）
     * @return 分类树形结构
     */
    List<CategoryDTO> getCategoryTree();
    
    /**
     * 获取一级分类列表
     * @return 一级分类列表
     */
    List<Category> getFirstLevelCategories();
    
    /**
     * 根据父分类ID获取子分类列表
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> getChildCategories(Long parentId);
    
    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类详情
     */
    Category getCategoryById(Long id);
    
    /**
     * 创建分类
     * @param category 分类信息
     * @return 创建的分类
     */
    Category createCategory(Category category);
    
    /**
     * 更新分类
     * @param category 分类信息
     * @return 更新后的分类
     */
    Category updateCategory(Category category);
    
    /**
     * 删除分类
     * @param id 分类ID
     */
    void deleteCategory(Long id);
}