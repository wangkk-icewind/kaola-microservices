package com.kaola.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证全局过滤器
 * 从 Authorization: Bearer <token> 中提取 userId，注入 X-User-Id 请求头
 *
 * @author Kaola Team
 */
@Slf4j
@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret:kaola-default-secret-key-for-jwt-token-generation}")
    private String secret;

    /**
     * 无需认证的路径前缀（公开接口）
     */
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/user/phone-login",
            "/api/user/send-code",
            "/api/user/login",
            "/api/admin/auth/",
            "/api/store/",
            "/api/masseur/",
            "/api/city/",
            "/api/product/",
            "/api/project/",
            "/api/category/",
            "/api/price/",
            "/api/promotion/",
            "/api/promotions/"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 公开接口直接放行
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");

        // 无 token：若接口需要认证则返回 401
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("请求 {} 无 Authorization header", path);
            return chain.filter(exchange); // 下游服务自行处理缺少 X-User-Id 的情况
        }

        // 解析 JWT
        String token = authHeader.substring(7);
        Long userId = getUserIdFromToken(token);

        if (userId == null) {
            log.warn("无效 JWT token，路径: {}", path);
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 将 userId 注入下游请求头
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .build();
        log.debug("JWT 解析成功: userId={}, path={}", userId, path);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Long getUserIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Object userId = claims.get("userId");
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            } else if (userId instanceof Long) {
                return (Long) userId;
            }
        } catch (Exception e) {
            log.warn("JWT 解析失败: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public int getOrder() {
        return -100; // 高优先级，在路由之前执行
    }
}
