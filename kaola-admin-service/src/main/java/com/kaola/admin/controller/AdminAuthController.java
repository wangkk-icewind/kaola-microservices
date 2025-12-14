package com.kaola.admin.controller;

import com.kaola.admin.service.AdminUserService;
import com.kaola.admin.vo.AdminLoginVO;
import com.kaola.common.core.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 管理后台认证接口
 *
 * @author Kaola Team
 */
@Tag(name = "管理后台认证接口", description = "管理员登录、登出等接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AdminAuthController {

    private final AdminUserService adminUserService;

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
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取已登录管理员的信息")
    @GetMapping("/userInfo")
    public Result<AdminLoginVO> getUserInfo(@RequestHeader(value = "X-Admin-Id", required = false) Long adminId) {
        if (adminId == null) {
            return Result.error("未登录");
        }
        // 简化处理，返回基本信息
        AdminLoginVO vo = new AdminLoginVO();
        vo.setId(adminId);
        return Result.success(vo);
    }
}
