package com.kaola.product.service;

import com.kaola.product.model.entity.StorePricing;

import java.math.BigDecimal;
import java.util.List;

/**
 * 门店价格系数配置服务接口
 *
 * @author Kaola Team
 */
public interface StorePricingService {

    /**
     * 获取所有配置列表
     *
     * @return 配置列表
     */
    List<StorePricing> getList();

    /**
     * 获取所有启用的配置
     *
     * @return 启用的配置列表
     */
    List<StorePricing> getEnabledList();

    /**
     * 根据ID获取配置详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    StorePricing getById(Long id);

    /**
     * 根据门店ID获取价格系数
     *
     * @param storeId 门店ID
     * @return 价格系数，如果未找到则返回1.0
     */
    BigDecimal getMultiplierByStoreId(Long storeId);

    /**
     * 创建配置
     *
     * @param pricing 配置信息
     * @return 是否成功
     */
    boolean create(StorePricing pricing);

    /**
     * 更新配置
     *
     * @param id      配置ID
     * @param pricing 配置信息
     * @return 是否成功
     */
    boolean update(Long id, StorePricing pricing);

    /**
     * 批量更新配置
     *
     * @param pricingList 配置列表
     * @return 是否成功
     */
    boolean batchUpdate(List<StorePricing> pricingList);

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
