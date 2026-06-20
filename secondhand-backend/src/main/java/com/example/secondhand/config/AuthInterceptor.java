package com.example.secondhand.config;

import com.example.secondhand.utils.JwtUtils;
import com.example.secondhand.utils.LoginUser;
import com.example.secondhand.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    /**
     * 可选认证路径：允许匿名访问，但如果携带了有效 token 则解析后注入 UserContext
     */
    private static final Set<String> OPTIONAL_AUTH_PREFIXES = Set.of(
            "/api/health",
            "/api/goods/list",
            "/api/goods/detail/",
            "/api/demand/list",
            "/api/demand/detail/"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestPath = request.getRequestURI();
        boolean isOptional = isOptionalAuthPath(requestPath);

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if (isOptional) {
                return true;  // 匿名访问放行
            }
            throw new AuthException(401, "未登录或登录已过期");
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            if (isOptional) {
                return true;  // token 无效也不阻断匿名接口
            }
            throw new AuthException(401, "Token无效或已过期");
        }

        // 解析 token 并设置 UserContext
        Claims claims = jwtUtils.parseToken(token);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(claims.get("userId", Long.class));
        loginUser.setUsername(claims.get("username", String.class));
        loginUser.setUserType(claims.get("userType", String.class));
        loginUser.setRole(claims.get("role", String.class));

        // ---- 权限校验（非可选接口才执行） ----
        if (!isOptional) {
            // /api/admin/** 必须 admin 身份
            if (requestPath.startsWith("/api/admin/") && !"admin".equals(loginUser.getUserType())) {
                throw new AuthException(403, "无权限访问");
            }
        }

        UserContext.set(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.remove();
    }

    /**
     * 判断是否为可选认证路径（匿名可访问）
     */
    private boolean isOptionalAuthPath(String path) {
        for (String prefix : OPTIONAL_AUTH_PREFIXES) {
            if (path.equals(prefix) || path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}