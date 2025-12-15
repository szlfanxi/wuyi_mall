package com.wuyimall.dto;

import java.util.List;

/**
 * 分类DTO，用于前端展示树形结构
 */
public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
    private List<CategoryDTO> children;
    
    // getter 和 setter 方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public Integer getSort() {
        return sort;
    }
    
    public void setSort(Integer sort) {
        this.sort = sort;
    }
    
    public List<CategoryDTO> getChildren() {
        return children;
    }
    
    public void setChildren(List<CategoryDTO> children) {
        this.children = children;
    }
}