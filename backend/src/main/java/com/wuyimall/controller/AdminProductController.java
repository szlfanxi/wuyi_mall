package com.wuyimall.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.wuyimall.service.AdminService;
import com.wuyimall.repository.ProductRepository;
import com.wuyimall.repository.CategoryRepository;
import com.wuyimall.repository.ProductImageRepository;
import com.wuyimall.entity.Product;
import com.wuyimall.entity.Category;
import com.wuyimall.entity.ProductImage;
import com.wuyimall.dto.ProductDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AdminProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductImageRepository productImageRepository;

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
    public ResponseEntity<Map<String, Object>> list(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
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
                
                // 处理商品图片列表
                List<String> imageUrls = new ArrayList<>();
                if (product.getImages() != null) {
                    for (ProductImage image : product.getImages()) {
                        imageUrls.add(image.getImageUrl());
                    }
                }
                productDTO.setImages(imageUrls);
                
                // 获取分类名称
            String categoryName = "未分类";
            if (product.getCategoryId() != null) {
                Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
                if (category != null) {
                    categoryName = category.getName();
                }
            }
                productDTO.setCategoryName(categoryName);
                
                result.add(productDTO);
            }
            
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 新增商品
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Product product, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(request);
            adminService.checkAdmin(userId);

            product.setId(null);
            product.setStatus(1); // 默认上架
            Product createdProduct = productRepository.save(product);
            
            response.put("success", true);
            response.put("data", createdProduct);
            response.put("message", "商品创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 更新商品
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> update(@RequestBody Product product, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(request);
            adminService.checkAdmin(userId);

            if (product.getId() == null) {
                throw new RuntimeException("缺少商品ID");
            }
            Product updatedProduct = productRepository.save(product);
            
            response.put("success", true);
            response.put("data", updatedProduct);
            response.put("message", "商品更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 删除商品（可选）
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestParam Long id, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(request);
            adminService.checkAdmin(userId);

            productRepository.deleteById(id);
            
            response.put("success", true);
            response.put("message", "商品删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 上架/下架
    @PostMapping("/changeStatus")
    public ResponseEntity<Map<String, Object>> changeStatus(@RequestParam Long id,
                               @RequestParam Integer status,
                               HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(request);
            adminService.checkAdmin(userId);

            Product p = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("商品不存在"));
            p.setStatus(status);
            productRepository.save(p);
            
            response.put("success", true);
            response.put("message", "状态更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 保存商品图片
    @PostMapping("/images")
    public ResponseEntity<Map<String, Object>> saveProductImages(@RequestBody Map<String, Object> requestBody,
                                                         HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(request);
            adminService.checkAdmin(userId);
            
            // 获取请求参数
            Long productId = Long.valueOf(requestBody.get("productId").toString());
            List<String> imageUrls = (List<String>) requestBody.get("imageUrls");
            
            // 验证商品是否存在
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("商品不存在"));
            
            // 保存商品图片
            for (String imageUrl : imageUrls) {
                ProductImage productImage = new ProductImage();
                productImage.setProductId(productId);
                productImage.setImageUrl(imageUrl);
                // 设置排序值
                productImage.setSort(0);
                
                // 保存到数据库
                productImageRepository.save(productImage);
            }
            
            response.put("success", true);
            response.put("message", "商品图片保存成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 删除商品图片
    @PostMapping("/images/delete")
    public ResponseEntity<Map<String, Object>> deleteImage(@RequestBody Map<String, Object> requestBody,
                                                          HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserId(request);
            adminService.checkAdmin(userId);
            
            // 获取请求参数
            Long productId = Long.valueOf(requestBody.get("productId").toString());
            String imageUrl = (String) requestBody.get("imageUrl");
            
            // 验证商品是否存在
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("商品不存在"));
            
            // 根据商品ID和图片URL查找图片
            List<ProductImage> productImages = productImageRepository.findByProductId(productId);
            boolean found = false;
            for (ProductImage productImage : productImages) {
                if (productImage.getImageUrl().equals(imageUrl)) {
                    // 删除图片
                    productImageRepository.delete(productImage);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                response.put("success", false);
                response.put("message", "图片不存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            response.put("success", true);
            response.put("message", "图片删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
