package com.wuyimall.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.wuyimall.service.AdminService;
import com.wuyimall.repository.ProductRepository;
import com.wuyimall.repository.CategoryRepository;
import com.wuyimall.entity.Product;
import com.wuyimall.entity.Category;
import com.wuyimall.dto.ProductDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AdminProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AdminService adminService;

    private Long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        return userId;
    }

    // 列表（返回带有分类名称的商品列表）
    @GetMapping("/list")
    public List<ProductDTO> list(HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);
        
        List<Product> products = productRepository.findAll();
        List<ProductDTO> result = new ArrayList<>();
        
        for (Product product : products) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setName(product.getName());
            productDTO.setPrice(product.getPrice());
            productDTO.setStock(product.getStock());
            productDTO.setCategoryId(product.getCategoryId());
            productDTO.setStatus(product.getStatus());
            productDTO.setDescription(product.getDescription());
            productDTO.setMainImage(product.getMainImage());
            
            // 获取分类名称
            Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
            String categoryName = category != null ? category.getName() : "未分类";
            productDTO.setCategoryName(categoryName);
            
            result.add(productDTO);
        }
        
        return result;
    }

    // 新增商品
    @PostMapping("/create")
    public Product create(@RequestBody Product product, HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);

        product.setId(null);
        product.setStatus(1); // 默认上架
        return productRepository.save(product);
    }

    // 更新商品
    @PostMapping("/update")
    public Product update(@RequestBody Product product, HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);

        if (product.getId() == null) {
            throw new RuntimeException("缺少商品ID");
        }
        return productRepository.save(product);
    }

    // 删除商品（可选）
    @PostMapping("/delete")
    public String delete(@RequestParam Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);

        productRepository.deleteById(id);
        return "删除成功";
    }

    // 上架/下架
    @PostMapping("/changeStatus")
    public String changeStatus(@RequestParam Long id,
                               @RequestParam Integer status,
                               HttpServletRequest request) {
        Long userId = getUserId(request);
        adminService.checkAdmin(userId);

        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        p.setStatus(status);
        productRepository.save(p);
        return "状态更新成功";
    }
}
