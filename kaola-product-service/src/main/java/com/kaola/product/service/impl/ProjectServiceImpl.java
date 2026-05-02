package com.kaola.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.product.client.MasseurServiceClient;
import com.kaola.product.dto.Result;
import com.kaola.product.model.entity.Masseur;
import com.kaola.product.model.entity.Project;
import com.kaola.product.model.entity.ProjectCategory;
import com.kaola.product.model.vo.CategoryVO;
import com.kaola.product.model.vo.PriceCalculationVO;
import com.kaola.product.model.vo.ProjectVO;
import com.kaola.product.repository.MasseurRepository;
import com.kaola.product.repository.ProjectCategoryRepository;
import com.kaola.product.repository.ProjectRepository;
import com.kaola.product.service.MasseurLevelPricingService;
import com.kaola.product.service.ProjectService;
import com.kaola.product.service.StorePricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final MasseurRepository masseurRepository;
    private final MasseurLevelPricingService masseurLevelPricingService;
    private final StorePricingService storePricingService;

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
        return getProjectsByMasseur(masseurId, null);
    }

    @Override
    public List<ProjectVO> getProjectsByMasseur(Long masseurId, Long categoryId) {
        log.info("获取技师的项目, masseurId: {}, categoryId: {}", masseurId, categoryId);

        try {
            // 调用技师服务获取技师的项目ID列表
            Result<List<Long>> result = masseurServiceClient.getMasseurProjects(masseurId);

            if (result == null || result.getData() == null || result.getData().isEmpty()) {
                log.warn("技师{}没有关联的项目", masseurId);
                return Collections.emptyList();
            }

            List<Long> projectIds = result.getData();
            log.info("技师{}关联的项目ID列表: {}", masseurId, projectIds);

            // 查询技师信息，获取等级和门店ID用于价格计算
            BigDecimal levelMultiplier = BigDecimal.ONE;
            BigDecimal storeMultiplier = BigDecimal.ONE;
            try {
                Masseur masseur = masseurRepository.selectById(masseurId);
                if (masseur != null) {
                    if (masseur.getLevel() != null) {
                        levelMultiplier = masseurLevelPricingService.getMultiplierByLevel(masseur.getLevel());
                    }
                    if (masseur.getStoreId() != null) {
                        storeMultiplier = storePricingService.getMultiplierByStoreId(masseur.getStoreId());
                    }
                }
            } catch (Exception ex) {
                log.warn("获取价格系数失败，使用默认系数1.0: {}", ex.getMessage());
            }
            log.info("技师{}价格系数: level={}, store={}", masseurId, levelMultiplier, storeMultiplier);

            // 根据项目ID列表查询项目详情，可选按分类过滤
            LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Project::getStatus, 1)
                        .eq(Project::getDeleted, 0)
                        .in(Project::getId, projectIds);
            if (categoryId != null) {
                queryWrapper.eq(Project::getCategoryId, categoryId);
            }

            final BigDecimal lm = levelMultiplier;
            final BigDecimal sm = storeMultiplier;
            List<Project> projects = projectRepository.selectList(queryWrapper);
            return projects.stream()
                    .map(p -> convertToProjectVO(p, lm, sm))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取技师项目列表失败, masseurId: {}", masseurId, e);
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
     * 将项目实体转换为VO（无价格系数，原价展示）
     */
    private ProjectVO convertToProjectVO(Project project) {
        return convertToProjectVO(project, BigDecimal.ONE, BigDecimal.ONE);
    }

    /**
     * 将项目实体转换为VO，应用技师等级系数和门店系数
     */
    private ProjectVO convertToProjectVO(Project project, BigDecimal levelMultiplier, BigDecimal storeMultiplier) {
        ProjectVO vo = new ProjectVO();
        vo.setId(project.getId());
        vo.setName(project.getName());
        vo.setDescription(project.getDescription());
        vo.setDuration(project.getDuration());

        BigDecimal basePrice = project.getBasePrice();
        // 应用等级系数和门店系数（时段系数在浏览时未知，由前端再乘）
        BigDecimal currentPrice = basePrice
                .multiply(levelMultiplier)
                .multiply(storeMultiplier)
                .setScale(0, RoundingMode.FLOOR);
        vo.setPrice(currentPrice);
        // 划线原价 = project.originalPrice × 相同系数（若未配置则等于 basePrice × 系数）
        BigDecimal rawOriginalPrice = project.getOriginalPrice() != null ? project.getOriginalPrice() : basePrice;
        vo.setOriginalPrice(rawOriginalPrice
                .multiply(levelMultiplier)
                .multiply(storeMultiplier)
                .setScale(0, RoundingMode.FLOOR));

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
