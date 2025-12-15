package com.wuyimall.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class UploadController {

    // 上传文件保存路径
    @Value("${file.upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "上传文件不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "只允许上传图片文件");
                return ResponseEntity.badRequest().body(response);
            }

            // 检查文件大小（限制为2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "图片大小不能超过2MB");
                return ResponseEntity.badRequest().body(response);
            }

            // 创建保存目录
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID().toString() + fileExtension;

            // 保存文件
            File dest = new File(dir, filename);
            file.transferTo(dest);

            // 构建文件访问URL
            String fileUrl = "http://localhost:8081/upload/" + filename;

            // 返回成功响应
            response.put("success", true);
            response.put("data", Map.of("url", fileUrl));
            response.put("message", "文件上传成功");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "文件上传失败");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
