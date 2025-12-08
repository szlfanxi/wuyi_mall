package com.wuyimall.repository;

import com.wuyimall.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 分类Repository接口
 * 继承JpaRepository，提供基本的CRUD操作
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 可以根据需要添加其他查询方法
}
