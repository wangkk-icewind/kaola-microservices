package com.kaola.auth.controller;

import com.kaola.auth.model.vo.AdminLoginVO;
import com.kaola.auth.service.AdminUserService;
import com.kaola.common.core.dto.Result;
import com.kaola.common.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理后台认证接口
 *
 * @author Kaola Team
 */
@Tag(name = "管理后台认证接口", description = "管理员登录、登出等接口")
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@Validated
public class AdminAuthController {

    private final AdminUserService adminUserService;
    private final JwtUtil jwtUtil;

    /**
     * 管理员登录
     */
    @Operation(summary = "管理员登录", description = "使用用户名密码登录")
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        AdminLoginVO vo = adminUserService.login(username.trim(), password);
        return Result.success(vo);
    }

    /**
     * 管理员登出
     */
    @Operation(summary = "管理员登出", description = "退出登录")
    @PostMapping("/logout")
    public Result<Boolean> logout() {
        // 客户端清除token即可
        return Result.success(true);
    }

    /**
     * 修改密码
     */
    @Operation(summary = "修改密码", description = "修改当前管理员密码")
    @PostMapping("/updatePassword")
    public Result<Boolean> updatePassword(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, String> body) {
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            userId = jwtUtil.getUserIdFromToken(authHeader.substring(7));
        }
        if (userId == null) {
            return Result.error("未登录");
        }
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            return Result.error("密码不能为空");
        }
        adminUserService.updatePassword(userId, oldPassword, newPassword);
        return Result.success(true);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取已登录管理员的信息")
    @GetMapping("/userInfo")
    public Result<AdminLoginVO> getUserInfo(
            @RequestHeader(value = "X-Admin-Id", required = false) Long adminId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = adminId;
        // 若没有 X-Admin-Id，直接从 JWT 解析
        if (userId == null && StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            userId = jwtUtil.getUserIdFromToken(token);
        }
        if (userId == null) {
            return Result.error("未登录");
        }
        AdminLoginVO vo = new AdminLoginVO();
        vo.setId(userId);
        return Result.success(vo);
    }
}
