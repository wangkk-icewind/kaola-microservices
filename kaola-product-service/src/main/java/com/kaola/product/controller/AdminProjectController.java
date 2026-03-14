package com.kaola.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.product.dto.Result;
import com.kaola.product.model.entity.Category;
import com.kaola.product.model.entity.Project;
import com.kaola.product.model.vo.PageVO;
import com.kaola.product.repository.CategoryRepository;
import com.kaola.product.repository.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理后台 - 项目管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "管理后台 - 项目管理", description = "服务项目的增删改查接口")
@RestController
@RequestMapping("/admin/project")
@RequiredArgsConstructor
public class AdminProjectController {

    private final ProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 分页查询项目列表
     */
    @Operation(summary = "分页查询项目列表", description = "支持名称搜索、分类筛选和状态筛选")
    @GetMapping("/list")
    public Result<PageVO<Project>> getProjectList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {

        log.info("分页查询项目列表, current: {}, pageSize: {}, name: {}, categoryId: {}, status: {}",
                 current, pageSize, name, categoryId, status);

        Page<Project> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();

        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Project::getName, name.trim());
        }

        if (categoryId != null) {
            wrapper.eq(Project::getCategoryId, categoryId);
        }

        if (status != null) {
            wrapper.eq(Project::getStatus, status);
        }

        wrapper.orderByDesc(Project::getCreateTime);

        IPage<Project> pageResult = projectRepository.selectPage(page, wrapper);

        // 获取所有分类信息，用于填充categoryName
        List<Category> allCategories = categoryRepository.selectList(null);
        Map<Long, String> categoryMap = allCategories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 填充前端需要的额外字段
        pageResult.getRecords().forEach(project -> {
            // 设置价格字段（与basePrice相同）
            project.setPrice(project.getBasePrice());
            project.setOriginalPrice(project.getBasePrice());

            // 设置销量（暂时为0或"-"，实际应从订单统计）
            project.setSalesCount(0);

            // 设置分类名称
            if (project.getCategoryId() != null) {
                project.setCategoryName(categoryMap.get(project.getCategoryId()));
            }
        });

        PageVO<Project> pageVO = PageVO.of(pageResult);

        return Result.success(pageVO);
    }

    /**
     * 获取项目详情
     */
    @Operation(summary = "获取项目详情", description = "根据ID获取项目详细信息")
    @GetMapping("/detail/{id}")
    public Result<Project> getProjectDetail(@PathVariable Long id) {
        log.info("获取项目详情, id: {}", id);

        Project project = projectRepository.selectById(id);
        if (project == null || project.getDeleted() == 1) {
            return Result.error("项目不存在");
        }

        // 填充前端需要的额外字段
        project.setPrice(project.getBasePrice());
        project.setOriginalPrice(project.getBasePrice());
        project.setSalesCount(0);

        // 设置分类名称
        if (project.getCategoryId() != null) {
            Category category = categoryRepository.selectById(project.getCategoryId());
            if (category != null) {
                project.setCategoryName(category.getName());
            }
        }

        return Result.success(project);
    }

    /**
     * 创建项目
     */
    @Operation(summary = "创建项目", description = "新建服务项目")
    @PostMapping("/create")
    public Result<Boolean> createProject(@RequestBody Project project) {
        log.info("创建项目, name: {}", project.getName());

        if (project.getStatus() == null) {
            project.setStatus(1);
        }

        int rows = projectRepository.insert(project);
        return rows > 0 ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 更新项目
     */
    @Operation(summary = "更新项目", description = "修改项目信息")
    @PutMapping("/update")
    public Result<Boolean> updateProject(@RequestBody Project project) {
        log.info("更新项目, id: {}, name: {}", project.getId(), project.getName());

        if (project.getId() == null) {
            return Result.error("项目ID不能为空");
        }

        Project existing = projectRepository.selectById(project.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("项目不存在");
        }

        int rows = projectRepository.updateById(project);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 删除项目（逻辑删除）
     */
    @Operation(summary = "删除项目", description = "逻辑删除项目")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteProject(@PathVariable Long id) {
        log.info("删除项目, id: {}", id);

        Project project = projectRepository.selectById(id);
        if (project == null || project.getDeleted() == 1) {
            return Result.error("项目不存在");
        }

        project.setDeleted(1);
        int rows = projectRepository.updateById(project);
        return rows > 0 ? Result.success(true) : Result.error("删除失败");
    }

    /**
     * 更新项目状态
     */
    @Operation(summary = "更新项目状态", description = "上架或下架项目")
    @PutMapping("/updateStatus")
    public Result<Boolean> updateProjectStatus(@RequestBody Project request) {
        log.info("更新项目状态, id: {}, status: {}", request.getId(), request.getStatus());

        if (request.getId() == null || request.getStatus() == null) {
            return Result.error("参数不完整");
        }

        Project project = projectRepository.selectById(request.getId());
        if (project == null || project.getDeleted() == 1) {
            return Result.error("项目不存在");
        }

        project.setStatus(request.getStatus());
        int rows = projectRepository.updateById(project);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }
}
