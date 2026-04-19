package com.kaola.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.product.client.MasseurServiceClient;
import com.kaola.product.dto.Result;
import com.kaola.product.model.entity.Project;
import com.kaola.product.model.entity.ProjectCategory;
import com.kaola.product.model.vo.CategoryVO;
import com.kaola.product.model.vo.PriceCalculationVO;
import com.kaola.product.model.vo.ProjectVO;
import com.kaola.product.repository.ProjectCategoryRepository;
import com.kaola.product.repository.ProjectRepository;
import com.kaola.product.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
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

    private final ProjectCategoryRepository categoryRepository;
    private final ProjectRepository projectRepository;
    private final MasseurServiceClient masseurServiceClient;

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
    public ProjectVO getProjectById(Long projectId) {
        if (projectId == null) return null;
        Project project = projectRepository.selectById(projectId);
        return project != null ? convertToProjectVO(project) : null;
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

        try {
            // 调用技师服务获取技师的项目ID列表
            Result<List<Long>> result = masseurServiceClient.getMasseurProjects(masseurId);

            if (result == null || result.getData() == null || result.getData().isEmpty()) {
                log.warn("技师{}没有关联的项目", masseurId);
                return Collections.emptyList();
            }

            List<Long> projectIds = result.getData();
            log.info("技师{}关联的项目ID列表: {}", masseurId, projectIds);

            // 根据项目ID列表查询项目详情
            LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Project::getStatus, 1)
                        .eq(Project::getDeleted, 0)
                        .in(Project::getId, projectIds);

            List<Project> projects = projectRepository.selectList(queryWrapper);
            return projects.stream().map(this::convertToProjectVO).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取技师项目列表失败, masseurId: {}", masseurId, e);
            // 降级处理：返回空列表而不是抛出异常
            return Collections.emptyList();
        }
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
        vo.setTechnique(project.getTechnique());
        vo.setIntroImages(project.getIntroImages());
        vo.setIntroVideo(project.getIntroVideo());
        vo.setSuitableImages(project.getSuitableImages());
        vo.setConditioningMethod(project.getConditioningMethod());
        vo.setConditioningImages(project.getConditioningImages());
        vo.setProcessSteps(project.getProcessSteps());
        vo.setContraindications(project.getContraindications());
        return vo;
    }
}
