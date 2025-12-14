package com.kaola.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.PageVO;
import com.kaola.common.core.dto.Result;
import com.kaola.user.model.entity.AdminUser;
import com.kaola.user.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - 管理员用户管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "管理后台 - 管理员用户管理", description = "管理员用户的增删改查接口")
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询管理员列表
     */
    @Operation(summary = "分页查询管理员列表", description = "支持用户名搜索、角色筛选和状态筛选")
    @GetMapping("/list")
    public Result<PageVO<AdminUser>> getAdminUserList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Integer status) {

        log.info("分页查询管理员列表, current: {}, pageSize: {}, username: {}, roleId: {}, status: {}",
                 current, pageSize, username, roleId, status);

        Page<AdminUser> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.trim().isEmpty()) {
            wrapper.like(AdminUser::getUsername, username.trim());
        }

        if (roleId != null) {
            wrapper.eq(AdminUser::getRoleId, roleId);
        }

        if (status != null) {
            wrapper.eq(AdminUser::getStatus, status);
        }

        wrapper.orderByDesc(AdminUser::getCreateTime);

        IPage<AdminUser> pageResult = adminUserService.page(page, wrapper);

        // Convert IPage to PageVO
        PageVO<AdminUser> pageVO = new PageVO<>();
        pageVO.setRecords(pageResult.getRecords());
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setCurrent(pageResult.getCurrent());
        pageVO.setPageSize(pageResult.getSize());
        pageVO.setPages(pageResult.getPages());

        return Result.success(pageVO);
    }

    /**
     * 获取所有管理员（不分页）
     */
    @Operation(summary = "获取所有管理员", description = "获取所有启用的管理员")
    @GetMapping("/all")
    public Result<List<AdminUser>> getAllAdminUsers() {
        log.info("获取所有管理员");

        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUser::getStatus, 1)
               .orderByAsc(AdminUser::getUsername);

        List<AdminUser> users = adminUserService.list(wrapper);
        return Result.success(users);
    }

    /**
     * 获取管理员详情
     */
    @Operation(summary = "获取管理员详情", description = "根据ID获取管理员详细信息")
    @GetMapping("/detail/{id}")
    public Result<AdminUser> getAdminUserDetail(@PathVariable Long id) {
        log.info("获取管理员详情, id: {}", id);

        AdminUser user = adminUserService.getById(id);
        if (user == null || user.getDeleted() == 1) {
            return Result.error("管理员不存在");
        }

        // Don't return password
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 创建管理员
     */
    @Operation(summary = "创建管理员", description = "新建管理员用户")
    @PostMapping("/create")
    public Result<Boolean> createAdminUser(@RequestBody AdminUser user) {
        log.info("创建管理员, username: {}", user.getUsername());

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error("密码不能为空");
        }

        // Check if username already exists
        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUser::getUsername, user.getUsername());
        long count = adminUserService.count(wrapper);
        if (count > 0) {
            return Result.error("用户名已存在");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        boolean success = adminUserService.save(user);
        return success ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 更新管理员
     */
    @Operation(summary = "更新管理员", description = "修改管理员信息")
    @PutMapping("/update")
    public Result<Boolean> updateAdminUser(@RequestBody AdminUser user) {
        log.info("更新管理员, id: {}, username: {}", user.getId(), user.getUsername());

        if (user.getId() == null) {
            return Result.error("管理员ID不能为空");
        }

        AdminUser existing = adminUserService.getById(user.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("管理员不存在");
        }

        // Don't update password through this endpoint
        user.setPassword(null);

        boolean success = adminUserService.updateById(user);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 删除管理员（逻辑删除）
     */
    @Operation(summary = "删除管理员", description = "逻辑删除管理员")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteAdminUser(@PathVariable Long id) {
        log.info("删除管理员, id: {}", id);

        AdminUser user = adminUserService.getById(id);
        if (user == null || user.getDeleted() == 1) {
            return Result.error("管理员不存在");
        }

        user.setDeleted(1);
        boolean success = adminUserService.updateById(user);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    /**
     * 更新管理员状态
     */
    @Operation(summary = "更新管理员状态", description = "启用或禁用管理员")
    @PutMapping("/updateStatus")
    public Result<Boolean> updateAdminUserStatus(@RequestBody AdminUser request) {
        log.info("更新管理员状态, id: {}, status: {}", request.getId(), request.getStatus());

        if (request.getId() == null || request.getStatus() == null) {
            return Result.error("参数不完整");
        }

        AdminUser user = adminUserService.getById(request.getId());
        if (user == null || user.getDeleted() == 1) {
            return Result.error("管理员不存在");
        }

        user.setStatus(request.getStatus());
        boolean success = adminUserService.updateById(user);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 重置管理员密码
     */
    @Operation(summary = "重置管理员密码", description = "重置管理员密码")
    @PostMapping("/resetPassword")
    public Result<Boolean> resetPassword(@RequestBody AdminUser request) {
        log.info("重置管理员密码, id: {}", request.getId());

        if (request.getId() == null) {
            return Result.error("管理员ID不能为空");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return Result.error("新密码不能为空");
        }

        AdminUser user = adminUserService.getById(request.getId());
        if (user == null || user.getDeleted() == 1) {
            return Result.error("管理员不存在");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        boolean success = adminUserService.updateById(user);
        return success ? Result.success(true) : Result.error("重置失败");
    }
}
