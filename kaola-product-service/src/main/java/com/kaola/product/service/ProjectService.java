package com.kaola.product.service;
import com.kaola.product.model.vo.CategoryVO;
import com.kaola.product.model.vo.PriceCalculationVO;

import com.kaola.product.model.vo.ProjectVO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 项目服务接口
 *
 * @author Kaola Team
 */
public interface ProjectService {

    /**
     * 获取项目分类
     *
     * @return 分类列表
     */
    List<CategoryVO> getCategories();

    /**
     * 获取分类下的项目
     *
     * @param categoryId 分类ID
     * @return 项目列表
     */
    List<ProjectVO> getProjectsByCategory(Long categoryId);

    /**
     * 获取技师的项目
     *
     * @param masseurId 技师ID
     * @return 项目列表
     */
    List<ProjectVO> getProjectsByMasseur(Long masseurId);

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
    PriceCalculationVO calculatePrice(Long projectId, Long masseurId, Long storeId, LocalDate date, LocalTime time);
}
