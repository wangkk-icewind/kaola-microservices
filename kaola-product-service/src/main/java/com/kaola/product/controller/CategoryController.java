package com.kaola.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.product.dto.Result;
import com.kaola.product.model.entity.ProjectCategory;
import com.kaola.product.model.vo.PageVO;
import com.kaola.product.repository.ProjectCategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目分类管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "项目分类管理", description = "项目分类的增删改查接口")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final ProjectCategoryRepository projectCategoryRepository;

    /**
     * 分页查询分类列表
     */
    @Operation(summary = "分页查询分类列表", description = "支持名称搜索")
    @GetMapping("/list")
    public Result<PageVO<ProjectCategory>> getCategoryList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String name) {

        log.info("分页查询分类列表, current: {}, pageSize: {}, name: {}", current, pageSize, name);

        Page<ProjectCategory> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<ProjectCategory> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(ProjectCategory::getDeleted, 0);

        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(ProjectCategory::getName, name.trim());
        }

        wrapper.orderByAsc(ProjectCategory::getSort);

        IPage<ProjectCategory> pageResult = projectCategoryRepository.selectPage(page, wrapper);
        PageVO<ProjectCategory> pageVO = PageVO.of(pageResult);

        return Result.success(pageVO);
    }

    /**
     * 获取所有分类（不分页）
     */
    @Operation(summary = "获取所有分类", description = "获取所有启用的分类，用于下拉选择")
    @GetMapping("/all")
    public Result<List<ProjectCategory>> getAllCategories() {
        log.info("获取所有分类");

        LambdaQueryWrapper<ProjectCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectCategory::getDeleted, 0)
               .eq(ProjectCategory::getStatus, 1)
               .orderByAsc(ProjectCategory::getSort);

        List<ProjectCategory> categories = projectCategoryRepository.selectList(wrapper);
        return Result.success(categories);
    }

    /**
     * 创建分类
     */
    @Operation(summary = "创建分类", description = "新建项目分类")
    @PostMapping("/create")
    public Result<Boolean> createCategory(@RequestBody ProjectCategory category) {
        log.info("创建分类, name: {}", category.getName());

        if (category.getStatus() == null) {
            category.setStatus(1);
        }

        if (category.getSort() == null) {
            category.setSort(0);
        }

        int rows = projectCategoryRepository.insert(category);
        return rows > 0 ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 更新分类
     */
    @Operation(summary = "更新分类", description = "修改分类信息")
    @PutMapping("/update")
    public Result<Boolean> updateCategory(@RequestBody ProjectCategory category) {
        log.info("更新分类, id: {}, name: {}", category.getId(), category.getName());

        if (category.getId() == null) {
            return Result.error("分类ID不能为空");
        }

        ProjectCategory existing = projectCategoryRepository.selectById(category.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("分类不存在");
        }

        int rows = projectCategoryRepository.updateById(category);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 删除分类（逻辑删除）
     */
    @Operation(summary = "删除分类", description = "逻辑删除分类")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        log.info("删除分类, id: {}", id);

        ProjectCategory category = projectCategoryRepository.selectById(id);
        if (category == null || category.getDeleted() == 1) {
            return Result.error("分类不存在");
        }

        category.setDeleted(1);
        int rows = projectCategoryRepository.updateById(category);
        return rows > 0 ? Result.success(true) : Result.error("删除失败");
    }
}
