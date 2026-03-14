package com.kaola.marketing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.marketing.mapper.MasseurLevelPricingMapper;
import com.kaola.marketing.mapper.StorePricingMapper;
import com.kaola.marketing.mapper.TimeSlotPricingMapper;
import com.kaola.marketing.model.dto.PriceCalculationRequest;
import com.kaola.marketing.model.entity.MasseurLevelPricing;
import com.kaola.marketing.model.entity.StorePricing;
import com.kaola.marketing.model.entity.TimeSlotPricing;
import com.kaola.marketing.model.vo.PriceCalculationVO;
import com.kaola.marketing.model.vo.PricingConfigVO;
import com.kaola.marketing.service.PricingConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 定价配置服务实现类
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PricingConfigServiceImpl implements PricingConfigService {

    private final MasseurLevelPricingMapper masseurLevelPricingMapper;
    private final TimeSlotPricingMapper timeSlotPricingMapper;
    private final StorePricingMapper storePricingMapper;

    @Override
    public PricingConfigVO getPricingConfig() {
        log.info("获取定价配置");

        PricingConfigVO config = new PricingConfigVO();

        // 1. 获取技师等级定价
        List<MasseurLevelPricing> masseurLevelPricings = masseurLevelPricingMapper.selectList(
                new LambdaQueryWrapper<MasseurLevelPricing>()
                        .eq(MasseurLevelPricing::getStatus, 1)
                        .orderByAsc(MasseurLevelPricing::getLevel)
        );

        List<PricingConfigVO.MasseurLevelPricingItem> masseurLevelItems = masseurLevelPricings.stream()
                .map(pricing -> {
                    PricingConfigVO.MasseurLevelPricingItem item = new PricingConfigVO.MasseurLevelPricingItem();
                    item.setLevel(pricing.getLevel());
                    item.setLevelName(pricing.getLevelName());
                    item.setMultiplier(pricing.getMultiplier());
                    return item;
                })
                .collect(Collectors.toList());

        config.setMasseurLevelPricing(masseurLevelItems);

        // 2. 获取时段定价
        List<TimeSlotPricing> timeSlotPricings = timeSlotPricingMapper.selectList(
                new LambdaQueryWrapper<TimeSlotPricing>()
                        .eq(TimeSlotPricing::getStatus, 1)
                        .orderByAsc(TimeSlotPricing::getSlotType)
        );

        List<PricingConfigVO.TimeSlotPricingItem> timeSlotItems = timeSlotPricings.stream()
                .map(pricing -> {
                    PricingConfigVO.TimeSlotPricingItem item = new PricingConfigVO.TimeSlotPricingItem();
                    item.setSlotType(pricing.getSlotType());
                    item.setSlotName(pricing.getSlotName());
                    item.setTimeRanges(pricing.getTimeRanges());
                    item.setDayOfWeek(pricing.getDayOfWeek());
                    item.setMultiplier(pricing.getMultiplier());
                    return item;
                })
                .collect(Collectors.toList());

        config.setTimeSlotPricing(timeSlotItems);

        // 3. 获取门店定价
        List<StorePricing> storePricings = storePricingMapper.selectList(
                new LambdaQueryWrapper<StorePricing>()
                        .eq(StorePricing::getStatus, 1)
                        .orderByAsc(StorePricing::getStoreId)
        );

        List<PricingConfigVO.StorePricingItem> storeItems = storePricings.stream()
                .map(pricing -> {
                    PricingConfigVO.StorePricingItem item = new PricingConfigVO.StorePricingItem();
                    item.setStoreId(pricing.getStoreId());
                    item.setStoreName(pricing.getStoreName());
                    item.setMultiplier(pricing.getMultiplier());
                    return item;
                })
                .collect(Collectors.toList());

        config.setStorePricing(storeItems);

        log.info("定价配置获取成功: 技师等级{}个, 时段{}个, 门店{}个",
                masseurLevelItems.size(), timeSlotItems.size(), storeItems.size());

        return config;
    }

    @Override
    public PriceCalculationVO calculatePrice(PriceCalculationRequest request) {
        log.info("计算价格: projectId={}, masseurLevel={}, storeId={}, appointmentTime={}",
                request.getProjectId(), request.getMasseurLevel(), request.getStoreId(), request.getAppointmentTime());

        // TODO: 在实际实现中，需要通过OpenFeign调用product-service获取项目基础价格
        // 这里使用固定值作为示例
        BigDecimal basePrice = new BigDecimal("188.00");

        // 1. 获取技师等级系数
        BigDecimal masseurMultiplier = getMasseurLevelMultiplier(request.getMasseurLevel());

        // 2. 获取时段系数
        BigDecimal timeSlotMultiplier = getTimeSlotMultiplier(request.getAppointmentTime());

        // 3. 获取门店系数
        BigDecimal storeMultiplier = getStoreMultiplier(request.getStoreId());

        // 4. 计算原价 = 基础价格 × 技师系数 × 时段系数 × 门店系数
        BigDecimal originalPrice = basePrice
                .multiply(masseurMultiplier)
                .multiply(timeSlotMultiplier)
                .multiply(storeMultiplier)
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        // 5. 计算加钟费用
        BigDecimal extraPrice = BigDecimal.ZERO;
        if (request.getExtraMinutes() != null && request.getExtraMinutes() > 0) {
            // 加钟单价 = 基础价格 / 60分钟
            BigDecimal pricePerMinute = basePrice.divide(new BigDecimal("60"), 4, BigDecimal.ROUND_HALF_UP);
            extraPrice = pricePerMinute
                    .multiply(new BigDecimal(request.getExtraMinutes()))
                    .multiply(timeSlotMultiplier)
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        // 6. 计算优惠折扣（暂时不实现优惠券逻辑）
        BigDecimal discountAmount = BigDecimal.ZERO;
        String discountReason = null;

        // 7. 计算最终价格 = 原价 + 加钟费用 - 优惠折扣
        BigDecimal finalPrice = originalPrice
                .add(extraPrice)
                .subtract(discountAmount)
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        // 8. 构建返回结果
        PriceCalculationVO result = PriceCalculationVO.builder()
                .basePrice(basePrice)
                .masseurLevelMultiplier(masseurMultiplier)
                .timeSlotMultiplier(timeSlotMultiplier)
                .storeMultiplier(storeMultiplier)
                .originalPrice(originalPrice)
                .extraPrice(extraPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .discountReason(discountReason)
                .build();

        log.info("价格计算完成: 基础价格={}, 原价={}, 加钟费用={}, 最终价格={}",
                basePrice, originalPrice, extraPrice, finalPrice);

        return result;
    }

    /**
     * 获取技师等级系数
     */
    private BigDecimal getMasseurLevelMultiplier(Integer level) {
        MasseurLevelPricing pricing = masseurLevelPricingMapper.selectOne(
                new LambdaQueryWrapper<MasseurLevelPricing>()
                        .eq(MasseurLevelPricing::getLevel, level)
                        .eq(MasseurLevelPricing::getStatus, 1)
        );
        return pricing != null ? pricing.getMultiplier() : BigDecimal.ONE;
    }

    /**
     * 获取时段系数
     */
    private BigDecimal getTimeSlotMultiplier(java.time.LocalDateTime appointmentTime) {
        // 获取星期几 (1-7)
        int dayOfWeek = appointmentTime.getDayOfWeek().getValue();
        // 获取时间 (HH:mm)
        String timeStr = String.format("%02d:%02d", appointmentTime.getHour(), appointmentTime.getMinute());

        // 查询所有启用的时段定价
        List<TimeSlotPricing> timeSlotPricings = timeSlotPricingMapper.selectList(
                new LambdaQueryWrapper<TimeSlotPricing>()
                        .eq(TimeSlotPricing::getStatus, 1)
        );

        // 匹配时段
        for (TimeSlotPricing pricing : timeSlotPricings) {
            if (matchesTimeSlot(pricing, dayOfWeek, timeStr)) {
                return pricing.getMultiplier();
            }
        }

        return BigDecimal.ONE;
    }

    /**
     * 判断是否匹配时段
     */
    private boolean matchesTimeSlot(TimeSlotPricing pricing, int dayOfWeek, String timeStr) {
        // 简化实现：这里应该解析JSON格式的dayOfWeek和timeRanges
        // 暂时返回true，使用第一个匹配的时段
        return true;
    }

    /**
     * 获取门店系数
     */
    private BigDecimal getStoreMultiplier(Long storeId) {
        StorePricing pricing = storePricingMapper.selectOne(
                new LambdaQueryWrapper<StorePricing>()
                        .eq(StorePricing::getStoreId, storeId)
                        .eq(StorePricing::getStatus, 1)
        );
        return pricing != null ? pricing.getMultiplier() : BigDecimal.ONE;
    }
}
