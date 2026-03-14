package com.kaola.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.product.mapper.MasseurLevelPricingMapper;
import com.kaola.product.mapper.StorePricingMapper;
import com.kaola.product.mapper.TimeSlotPricingMapper;
import com.kaola.product.model.entity.MasseurLevelPricing;
import com.kaola.product.model.entity.StorePricing;
import com.kaola.product.model.entity.TimeSlotPricing;
import com.kaola.product.model.vo.PricingConfigVO;
import com.kaola.product.service.PricingConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 定价配置服务实现
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PricingConfigServiceImpl implements PricingConfigService {

    private final MasseurLevelPricingMapper masseurLevelPricingMapper;
    private final StorePricingMapper storePricingMapper;
    private final TimeSlotPricingMapper timeSlotPricingMapper;

    @Override
    public PricingConfigVO getPricingConfig() {
        log.info("获取定价配置");

        PricingConfigVO config = new PricingConfigVO();

        // 获取技师等级定价（只返回启用状态的）
        List<MasseurLevelPricing> masseurLevelPricings = masseurLevelPricingMapper.selectList(
                new LambdaQueryWrapper<MasseurLevelPricing>()
                        .eq(MasseurLevelPricing::getStatus, 1)
                        .orderByAsc(MasseurLevelPricing::getLevel)
        );
        config.setMasseurLevelPricing(
                masseurLevelPricings.stream()
                        .map(this::convertToMasseurLevelPricingItem)
                        .collect(Collectors.toList())
        );

        // 获取时段定价（只返回启用状态的）
        List<TimeSlotPricing> timeSlotPricings = timeSlotPricingMapper.selectList(
                new LambdaQueryWrapper<TimeSlotPricing>()
                        .eq(TimeSlotPricing::getStatus, 1)
                        .orderByAsc(TimeSlotPricing::getSlotType)
        );
        config.setTimeSlotPricing(
                timeSlotPricings.stream()
                        .map(this::convertToTimeSlotPricingItem)
                        .collect(Collectors.toList())
        );

        // 获取门店定价（只返回启用状态的）
        List<StorePricing> storePricings = storePricingMapper.selectList(
                new LambdaQueryWrapper<StorePricing>()
                        .eq(StorePricing::getStatus, 1)
                        .orderByAsc(StorePricing::getStoreId)
        );
        config.setStorePricing(
                storePricings.stream()
                        .map(this::convertToStorePricingItem)
                        .collect(Collectors.toList())
        );

        log.info("定价配置获取成功: 技师等级{}条, 时段{}条, 门店{}条",
                masseurLevelPricings.size(), timeSlotPricings.size(), storePricings.size());

        return config;
    }

    /**
     * 转换技师等级定价实体为VO项
     */
    private PricingConfigVO.MasseurLevelPricingItem convertToMasseurLevelPricingItem(MasseurLevelPricing entity) {
        PricingConfigVO.MasseurLevelPricingItem item = new PricingConfigVO.MasseurLevelPricingItem();
        item.setLevel(entity.getLevel());
        item.setLevelName(entity.getLevelName());
        item.setMultiplier(entity.getMultiplier());
        return item;
    }

    /**
     * 转换时段定价实体为VO项
     */
    private PricingConfigVO.TimeSlotPricingItem convertToTimeSlotPricingItem(TimeSlotPricing entity) {
        PricingConfigVO.TimeSlotPricingItem item = new PricingConfigVO.TimeSlotPricingItem();
        item.setSlotType(entity.getSlotType());
        item.setSlotName(entity.getSlotName());
        item.setMultiplier(entity.getMultiplier());
        return item;
    }

    /**
     * 转换门店定价实体为VO项
     */
    private PricingConfigVO.StorePricingItem convertToStorePricingItem(StorePricing entity) {
        PricingConfigVO.StorePricingItem item = new PricingConfigVO.StorePricingItem();
        item.setStoreId(entity.getStoreId());
        item.setStoreName(entity.getStoreName());
        item.setMultiplier(entity.getMultiplier());
        return item;
    }
}
