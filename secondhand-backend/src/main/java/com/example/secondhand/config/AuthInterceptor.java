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

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthException(401, "未登录或登录已过期");
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            throw new AuthException(401, "Token无效或已过期");
        }

        Claims claims = jwtUtils.parseToken(token);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(claims.get("userId", Long.class));
        loginUser.setUsername(claims.get("username", String.class));
        loginUser.setUserType(claims.get("userType", String.class));
        loginUser.setRole(claims.get("role", String.class));

        // ---- 权限校验 ----
        String requestPath = request.getRequestURI();
        // /api/admin/** 必须 admin 身份（/api/admin/login 已排除，不会进入这里）
        if (requestPath.startsWith("/api/admin/") && !"admin".equals(loginUser.getUserType())) {
            throw new AuthException(403, "无权限访问");
        }

        UserContext.set(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.remove();
    }
}