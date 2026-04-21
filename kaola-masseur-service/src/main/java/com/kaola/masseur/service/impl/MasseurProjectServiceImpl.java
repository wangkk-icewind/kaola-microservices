package com.kaola.masseur.service.impl;

import com.kaola.masseur.mapper.MasseurProjectMapper;
import com.kaola.masseur.model.entity.MasseurProject;
import com.kaola.masseur.service.MasseurProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 技师-项目关联服务实现
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MasseurProjectServiceImpl implements MasseurProjectService {

    private final MasseurProjectMapper masseurProjectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setMasseurProjects(Long masseurId, List<Long> projectIds) {
        log.info("设置技师{}的项目列表: {}", masseurId, projectIds);

        // 删除旧关联
        masseurProjectMapper.deleteByMasseurId(masseurId);

        // 添加新关联
        if (projectIds != null && !projectIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Object pid : projectIds) {
                MasseurProject masseurProject = new MasseurProject();
                masseurProject.setMasseurId(masseurId);
                masseurProject.setProjectId(((Number) pid).longValue());
                masseurProject.setCreatedAt(now);
                masseurProject.setUpdatedAt(now);
                masseurProjectMapper.insert(masseurProject);
            }
        }
    }

    @Override
    public void addMasseurProject(Long masseurId, Long projectId) {
        log.info("添加技师{}的项目关联: {}", masseurId, projectId);

        // 检查是否已存在
        int exists = masseurProjectMapper.existsByMasseurIdAndProjectId(masseurId, projectId);
        if (exists > 0) {
            log.warn("技师{}的项目{}关联已存在", masseurId, projectId);
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        MasseurProject masseurProject = new MasseurProject();
        masseurProject.setMasseurId(masseurId);
        masseurProject.setProjectId(projectId);
        masseurProject.setCreatedAt(now);
        masseurProject.setUpdatedAt(now);
        masseurProjectMapper.insert(masseurProject);
    }

    @Override
    public void removeMasseurProject(Long masseurId, Long projectId) {
        log.info("删除技师{}的项目关联: {}", masseurId, projectId);
        masseurProjectMapper.deleteByMasseurIdAndProjectId(masseurId, projectId);
    }

    @Override
    public List<Long> getMasseurProjectIds(Long masseurId) {
        return masseurProjectMapper.findProjectIdsByMasseurId(masseurId);
    }

    @Override
    public List<Long> getMasseursByProject(Long projectId) {
        return masseurProjectMapper.findMasseurIdsByProjectId(projectId);
    }
}
