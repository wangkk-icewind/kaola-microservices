package com.kaola.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.PageVO;
import com.kaola.common.core.dto.Result;
import com.kaola.user.model.entity.AdminRole;
import com.kaola.user.service.AdminRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - 角色权限管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "管理后台 - 角色权限管理", description = "角色权限的增删改查接口")
@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    /**
     * 分页查询角色列表
     */
    @Operation(summary = "分页查询角色列表", description = "支持名称搜索和状态筛选")
    @GetMapping("/list")
    public Result<PageVO<AdminRole>> getRoleList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {

        log.info("分页查询角色列表, current: {}, pageSize: {}, name: {}, status: {}",
                 current, pageSize, name, status);

        Page<AdminRole> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();

        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(AdminRole::getName, name.trim());
        }

        if (status != null) {
            wrapper.eq(AdminRole::getStatus, status);
        }

        wrapper.orderByDesc(AdminRole::getCreateTime);

        IPage<AdminRole> pageResult = adminRoleService.page(page, wrapper);

        // Convert IPage to PageVO
        PageVO<AdminRole> pageVO = new PageVO<>();
        pageVO.setRecords(pageResult.getRecords());
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setCurrent(pageResult.getCurrent());
        pageVO.setPageSize(pageResult.getSize());
        pageVO.setPages(pageResult.getPages());

        return Result.success(pageVO);
    }

    /**
     * 获取所有角色（不分页）
     */
    @Operation(summary = "获取所有角色", description = "获取所有启用的角色，用于下拉选择")
    @GetMapping("/all")
    public Result<List<AdminRole>> getAllRoles() {
        log.info("获取所有角色");

        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getStatus, 1)
               .orderByAsc(AdminRole::getName);

        List<AdminRole> roles = adminRoleService.list(wrapper);
        return Result.success(roles);
    }

    /**
     * 获取角色详情
     */
    @Operation(summary = "获取角色详情", description = "根据ID获取角色详细信息")
    @GetMapping("/detail/{id}")
    public Result<AdminRole> getRoleDetail(@PathVariable Long id) {
        log.info("获取角色详情, id: {}", id);

        AdminRole role = adminRoleService.getById(id);
        if (role == null || role.getDeleted() == 1) {
            return Result.error("角色不存在");
        }

        return Result.success(role);
    }

    /**
     * 创建角色
     */
    @Operation(summary = "创建角色", description = "新建角色")
    @PostMapping("/create")
    public Result<Boolean> createRole(@RequestBody AdminRole role) {
        log.info("创建角色, name: {}", role.getName());

        if (role.getName() == null || role.getName().trim().isEmpty()) {
            return Result.error("角色名称不能为空");
        }

        // Check if role name already exists
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getName, role.getName());
        long count = adminRoleService.count(wrapper);
        if (count > 0) {
            return Result.error("角色名称已存在");
        }

        if (role.getStatus() == null) {
            role.setStatus(1);
        }

        boolean success = adminRoleService.save(role);
        return success ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 更新角色
     */
    @Operation(summary = "更新角色", description = "修改角色信息")
    @PutMapping("/update")
    public Result<Boolean> updateRole(@RequestBody AdminRole role) {
        log.info("更新角色, id: {}, name: {}", role.getId(), role.getName());

        if (role.getId() == null) {
            return Result.error("角色ID不能为空");
        }

        AdminRole existing = adminRoleService.getById(role.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("角色不存在");
        }

        boolean success = adminRoleService.updateById(role);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 删除角色（逻辑删除）
     */
    @Operation(summary = "删除角色", description = "逻辑删除角色")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteRole(@PathVariable Long id) {
        log.info("删除角色, id: {}", id);

        AdminRole role = adminRoleService.getById(id);
        if (role == null || role.getDeleted() == 1) {
            return Result.error("角色不存在");
        }

        role.setDeleted(1);
        boolean success = adminRoleService.updateById(role);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    /**
     * 更新角色状态
     */
    @Operation(summary = "更新角色状态", description = "启用或禁用角色")
    @PutMapping("/updateStatus")
    public Result<Boolean> updateRoleStatus(@RequestBody AdminRole request) {
        log.info("更新角色状态, id: {}, status: {}", request.getId(), request.getStatus());

        if (request.getId() == null || request.getStatus() == null) {
            return Result.error("参数不完整");
        }

        AdminRole role = adminRoleService.getById(request.getId());
        if (role == null || role.getDeleted() == 1) {
            return Result.error("角色不存在");
        }

        role.setStatus(request.getStatus());
        boolean success = adminRoleService.updateById(role);
        return success ? Result.success(true) : Result.error("更新失败");
    }
}
