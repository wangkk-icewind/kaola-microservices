package com.kaola.product.service;

import com.kaola.product.model.entity.TimeSlotPricing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 时段类型价格系数配置服务接口
 *
 * @author Kaola Team
 */
public interface TimeSlotPricingService {

    /**
     * 获取所有配置列表
     *
     * @return 配置列表
     */
    List<TimeSlotPricing> getList();

    /**
     * 获取所有启用的配置
     *
     * @return 启用的配置列表
     */
    List<TimeSlotPricing> getEnabledList();

    /**
     * 根据ID获取配置详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    TimeSlotPricing getById(Long id);

    /**
     * 根据时间获取价格系数（自动匹配时段类型）
     *
     * @param dateTime 预约时间
     * @return 价格系数，如果未找到则返回1.0
     */
    BigDecimal getMultiplierByDateTime(LocalDateTime dateTime);

    /**
     * 创建配置
     *
     * @param pricing 配置信息
     * @return 是否成功
     */
    boolean create(TimeSlotPricing pricing);

    /**
     * 更新配置
     *
     * @param id      配置ID
     * @param pricing 配置信息
     * @return 是否成功
     */
    boolean update(Long id, TimeSlotPricing pricing);

    /**
     * 批量更新配置
     *
     * @param pricingList 配置列表
     * @return 是否成功
     */
    boolean batchUpdate(List<TimeSlotPricing> pricingList);

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
