package com.wuyimall.repository;

import com.wuyimall.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 分类Repository接口
 * 继承JpaRepository，提供基本的CRUD操作
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * 根据父分类ID查询子分类
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> findByParentIdOrderBySortAsc(Long parentId);
    
    /**
     * 查询所有一级分类（parentId为null的分类）
     * @return 一级分类列表
     */
    List<Category> findByParentIdIsNullOrderBySortAsc();
}
