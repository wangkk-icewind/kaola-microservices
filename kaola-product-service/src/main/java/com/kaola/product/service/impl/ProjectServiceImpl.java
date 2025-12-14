package com.kaola.product.service.impl;

import com.kaola.product.model.vo.CategoryVO;
import com.kaola.product.model.vo.PriceCalculationVO;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.product.model.entity.Project;
import com.kaola.product.model.entity.ProjectCategory;
import com.kaola.product.model.vo.ProjectVO;
import com.kaola.product.mapper.ProjectCategoryMapper;
import com.kaola.product.mapper.ProjectMapper;
import com.kaola.product.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目服务实现类 - 从数据库读取真实数据
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectCategoryMapper categoryRepository;
    private final ProjectMapper projectRepository;

    @Override
    public List<CategoryVO> getCategories() {
        log.info("获取项目分类");

        LambdaQueryWrapper<ProjectCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectCategory::getStatus, 1)
                    .eq(ProjectCategory::getDeleted, 0)
                    .orderByAsc(ProjectCategory::getSort);

        List<ProjectCategory> categories = categoryRepository.selectList(queryWrapper);
        return categories.stream().map(this::convertToCategoryVO).collect(Collectors.toList());
    }

    @Override
    public List<ProjectVO> getProjectsByCategory(Long categoryId) {
        log.info("获取分类下的项目, categoryId: {}", categoryId);

        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getStatus, 1)
                    .eq(Project::getDeleted, 0);
        if (categoryId != null) {
            queryWrapper.eq(Project::getCategoryId, categoryId);
        }

        List<Project> projects = projectRepository.selectList(queryWrapper);
        return projects.stream().map(this::convertToProjectVO).collect(Collectors.toList());
    }

    @Override
    public List<ProjectVO> getProjectsByMasseur(Long masseurId) {
        log.info("获取技师的项目, masseurId: {}", masseurId);
        // 返回所有项目，后续可通过关联表查询
        return getProjectsByCategory(null);
    }

    @Override
    public PriceCalculationVO calculatePrice(Long projectId, Long masseurId, Long storeId, LocalDate date, LocalTime time) {
        log.info("计算价格, projectId: {}, masseurId: {}, storeId: {}, date: {}, time: {}",
            projectId, masseurId, storeId, date, time);

        Project project = projectRepository.selectById(projectId);
        PriceCalculationVO vo = new PriceCalculationVO();
        if (project != null) {
            vo.setOriginalPrice(project.getBasePrice());
            vo.setFinalPrice(project.getBasePrice());
        } else {
            vo.setOriginalPrice(BigDecimal.ZERO);
            vo.setFinalPrice(BigDecimal.ZERO);
        }
        return vo;
    }

    /**
     * 将分类实体转换为VO
     */
    private CategoryVO convertToCategoryVO(ProjectCategory category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setIcon(category.getIcon());
        vo.setSort(category.getSort());
        return vo;
    }

    /**
     * 将项目实体转换为VO
     */
    private ProjectVO convertToProjectVO(Project project) {
        ProjectVO vo = new ProjectVO();
        vo.setId(project.getId());
        vo.setName(project.getName());
        vo.setDescription(project.getDescription());
        vo.setDuration(project.getDuration());
        vo.setPrice(project.getBasePrice());
        // 解析图片JSON获取第一张
        String images = project.getImages();
        if (images != null && images.startsWith("[") && images.contains("\"")) {
            String firstImage = images.split("\"")[1];
            vo.setImage(firstImage);
        }
        return vo;
    }
}
