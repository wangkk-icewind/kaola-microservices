package com.kaola.product.service;

import com.kaola.product.model.entity.MasseurLevelPricing;

import java.math.BigDecimal;
import java.util.List;

/**
 * 技师等级价格系数配置服务接口
 *
 * @author Kaola Team
 */
public interface MasseurLevelPricingService {

    /**
     * 获取所有配置列表
     *
     * @return 配置列表
     */
    List<MasseurLevelPricing> getList();

    /**
     * 获取所有启用的配置
     *
     * @return 启用的配置列表
     */
    List<MasseurLevelPricing> getEnabledList();

    /**
     * 根据ID获取配置详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    MasseurLevelPricing getById(Long id);

    /**
     * 根据等级获取价格系数
     *
     * @param level 技师等级
     * @return 价格系数，如果未找到则返回1.0
     */
    BigDecimal getMultiplierByLevel(Integer level);

    /**
     * 创建配置
     *
     * @param pricing 配置信息
     * @return 是否成功
     */
    boolean create(MasseurLevelPricing pricing);

    /**
     * 更新配置
     *
     * @param id      配置ID
     * @param pricing 配置信息
     * @return 是否成功
     */
    boolean update(Long id, MasseurLevelPricing pricing);

    /**
     * 批量更新配置
     *
     * @param pricingList 配置列表
     * @return 是否成功
     */
    boolean batchUpdate(List<MasseurLevelPricing> pricingList);

    /**
     * 删除配置
     *
     * @param id 配置ID
     * @return 是否成功
     */
    boolean delete(Long id);

    /**
     * 更新状态
     *
     * @param id     配置ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
}
