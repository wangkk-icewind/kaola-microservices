package com.kaola.product.controller;

import com.kaola.product.dto.Result;
import com.kaola.product.model.vo.CategoryVO;
import com.kaola.product.model.vo.PriceCalculationVO;
import com.kaola.product.model.vo.ProjectVO;
import com.kaola.product.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 项目接口
 *
 * @author Kaola Team
 */
@Tag(name = "项目接口", description = "项目分类、列表、价格计算等接口")
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 获取项目分类
     *
     * @return 分类列表
     */
    @Operation(summary = "获取项目分类", description = "获取所有项目分类列表")
    @GetMapping("/categories")
    public Result<List<CategoryVO>> getCategories() {
        List<CategoryVO> categories = projectService.getCategories();
        return Result.success(categories);
    }

    /**
     * 获取项目列表
     *
     * @param categoryId 分类ID
     * @param masseurId  技师ID
     * @return 项目列表
     */
    @Operation(summary = "获取项目详情", description = "根据项目ID获取项目详情")
    @GetMapping("/{id}")
    public Result<ProjectVO> getProjectById(
            @Parameter(description = "项目ID", required = true)
            @PathVariable Long id) {
        ProjectVO project = projectService.getProjectById(id);
        return project != null ? Result.success(project) : Result.error("项目不存在");
    }

    @Operation(summary = "获取项目列表", description = "根据分类或技师获取项目列表")
    @GetMapping("/list")
    public Result<List<ProjectVO>> getProjectList(
            @Parameter(description = "分类ID")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "技师ID")
            @RequestParam(required = false) Long masseurId) {
        List<ProjectVO> projects;
        if (masseurId != null) {
            projects = projectService.getProjectsByMasseur(masseurId);
        } else if (categoryId != null) {
            projects = projectService.getProjectsByCategory(categoryId);
        } else {
            projects = projectService.getProjectsByCategory(null);
        }
        return Result.success(projects);
    }

    /**
     * 计算价格
     *
     * @param projectId 项目ID
     * @param masseurId 技师ID
     * @param storeId   门店ID
     * @param date      日期
     * @param time      时间
     * @return 价格计算结果
     */
    @Operation(summary = "计算价格", description = "根据项目、技师、门店、日期时间计算最终价格")
    @GetMapping("/price")
    public Result<PriceCalculationVO> calculatePrice(
            @Parameter(description = "项目ID", required = true)
            @RequestParam @NotNull(message = "项目ID不能为空") Long projectId,
            @Parameter(description = "技师ID", required = true)
            @RequestParam @NotNull(message = "技师ID不能为空") Long masseurId,
            @Parameter(description = "门店ID", required = true)
            @RequestParam @NotNull(message = "门店ID不能为空") Long storeId,
            @Parameter(description = "日期", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @Parameter(description = "时间", required = true)
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time) {
        PriceCalculationVO priceCalculation = projectService.calculatePrice(projectId, masseurId, storeId, date, time);
        return Result.success(priceCalculation);
    }
}
