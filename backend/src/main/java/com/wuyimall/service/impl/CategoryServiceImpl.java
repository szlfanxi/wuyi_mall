package com.wuyimall.service.impl;

import com.wuyimall.dto.CategoryDTO;
import com.wuyimall.entity.Category;
import com.wuyimall.repository.CategoryRepository;
import com.wuyimall.repository.ProductRepository;
import com.wuyimall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Override
    public List<CategoryDTO> getCategoryTree() {
        List<CategoryDTO> categoryTree = new ArrayList<>();
        
        // 获取所有一级分类
        List<Category> firstLevelCategories = categoryRepository.findByParentIdIsNullOrderBySortAsc();
        
        // 为每个一级分类构建二级分类
        for (Category firstLevel : firstLevelCategories) {
            CategoryDTO firstLevelDTO = convertToDTO(firstLevel);
            
            // 获取当前一级分类下的所有二级分类
            List<Category> secondLevelCategories = categoryRepository.findByParentIdOrderBySortAsc(firstLevel.getId());
            List<CategoryDTO> secondLevelDTOs = new ArrayList<>();
            
            // 转换二级分类为DTO
            for (Category secondLevel : secondLevelCategories) {
                secondLevelDTOs.add(convertToDTO(secondLevel));
            }
            
            // 设置二级分类为一级分类的子分类
            firstLevelDTO.setChildren(secondLevelDTOs);
            categoryTree.add(firstLevelDTO);
        }
        
        return categoryTree;
    }
    
    @Override
    public List<Category> getFirstLevelCategories() {
        return categoryRepository.findByParentIdIsNullOrderBySortAsc();
    }
    
    @Override
    public List<Category> getChildCategories(Long parentId) {
        return categoryRepository.findByParentIdOrderBySortAsc(parentId);
    }
    
    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }
    
    @Override
    @Transactional
    public Category createCategory(Category category) {
        // 验证：如果是二级分类，确保父分类存在
        if (category.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(category.getParentId()).orElse(null);
            if (parentCategory == null) {
                throw new RuntimeException("父分类不存在");
            }
            
            // 验证：确保不会创建三级或更高级别的分类
            if (parentCategory.getParentId() != null) {
                throw new RuntimeException("只能创建二级分类");
            }
        }
        
        // 设置默认排序值（如果未提供）
        if (category.getSort() == null) {
            category.setSort(0);
        }
        
        return categoryRepository.save(category);
    }
    
    @Override
    @Transactional
    public Category updateCategory(Category category) {
        // 验证分类是否存在
        Category existingCategory = categoryRepository.findById(category.getId()).orElse(null);
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 验证：如果是二级分类，确保父分类存在
        if (category.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(category.getParentId()).orElse(null);
            if (parentCategory == null) {
                throw new RuntimeException("父分类不存在");
            }
            
            // 验证：确保不会创建三级或更高级别的分类
            if (parentCategory.getParentId() != null) {
                throw new RuntimeException("只能创建二级分类");
            }
        }
        
        return categoryRepository.save(category);
    }
    
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        // 验证分类是否存在
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 验证：如果是一级分类，检查是否有子分类
        if (category.getParentId() == null) {
            List<Category> children = categoryRepository.findByParentIdOrderBySortAsc(id);
            if (!children.isEmpty()) {
                throw new RuntimeException("该分类下有子分类，无法删除");
            }
        }
        
        // 可以添加检查是否有商品关联到该分类的逻辑
        // 例如：if (productRepository.countByCategoryId(id) > 0) {
        //         throw new RuntimeException("该分类下有商品，无法删除");
        //     }
        
        categoryRepository.deleteById(id);
    }
    
    /**
     * 将Category实体转换为CategoryDTO
     * @param category 分类实体
     * @return 分类DTO
     */
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setParentId(category.getParentId());
        dto.setSort(category.getSort());
        dto.setChildren(new ArrayList<>());
        return dto;
    }
}