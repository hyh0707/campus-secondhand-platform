package com.example.secondhand.controller;

import com.example.secondhand.common.BusinessException;
import com.example.secondhand.common.Result;
import com.example.secondhand.vo.UploadImageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "webp"));
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.access-prefix}")
    private String accessPrefix;

    @PostMapping("/image")
    public Result<UploadImageVO> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("图片大小不能超过 5MB");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名无效");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("仅支持 jpg、jpeg、png、webp 格式");
        }

        // 解析为绝对路径（避免 Tomcat 将相对路径解析到临时目录）
        Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
        // 按日期分目录
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String fileName = UUID.randomUUID().toString() + "." + extension;

        // 确保目录存在
        Path targetDir = basePath.resolve(dateDir);
        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            log.error("创建上传目录失败", e);
            throw new BusinessException("文件上传失败");
        }

        // 保存文件
        Path targetFile = targetDir.resolve(fileName);
        try {
            file.transferTo(targetFile.toFile());
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败");
        }

        String url = accessPrefix + dateDir + "/" + fileName;
        log.info("图片上传成功: {}", url);
        return Result.success(new UploadImageVO(url));
    }
}