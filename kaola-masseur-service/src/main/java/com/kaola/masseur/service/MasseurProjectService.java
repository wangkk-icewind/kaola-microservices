package com.kaola.masseur.service;

import java.util.List;

/**
 * 技师-项目关联服务接口
 *
 * @author Kaola Team
 */
public interface MasseurProjectService {

    /**
     * 批量设置技师的项目列表（先删除旧关联，再添加新关联）
     *
     * @param masseurId 技师ID
     * @param projectIds 项目ID列表
     */
    void setMasseurProjects(Long masseurId, List<Long> projectIds);

    /**
     * 添加技师的项目关联
     *
     * @param masseurId 技师ID
     * @param projectId 项目ID
     */
    void addMasseurProject(Long masseurId, Long projectId);

    /**
     * 删除技师的项目关联
     *
     * @param masseurId 技师ID
     * @param projectId 项目ID
     */
    void removeMasseurProject(Long masseurId, Long projectId);

    /**
     * 获取技师的项目ID列表
     *
     * @param masseurId 技师ID
     * @return 项目ID列表
     */
    List<Long> getMasseurProjectIds(Long masseurId);

    /**
     * 获取可做指定项目的技师ID列表
     *
     * @param projectId 项目ID
     * @return 技师ID列表
     */
    List<Long> getMasseursByProject(Long projectId);
}
