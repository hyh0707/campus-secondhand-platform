package com.example.secondhand.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.access-prefix}")
    private String accessPrefix;

    // ==================== CORS ====================

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://127.0.0.1:5173",
                        "http://127.0.0.1:5174"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    // ==================== 拦截器 ====================

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        // 健康检查
                        "/api/health",
                        // 用户注册登录
                        "/api/user/register",
                        "/api/user/login",
                        // 管理员登录
                        "/api/admin/login",
                        // 商品公开查询
                        "/api/goods/list",
                        "/api/goods/detail/**",
                        // 求购公开查询
                        "/api/demand/list",
                        "/api/demand/detail/**",
                        // 静态资源
                        "/upload/**"
                );
    }

    // ==================== 静态资源 ====================

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解析为绝对路径，确保 Spring 能找到上传目录
        String absolutePath = Paths.get(uploadPath).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler(accessPrefix + "**")
                .addResourceLocations(absolutePath);
    }
}