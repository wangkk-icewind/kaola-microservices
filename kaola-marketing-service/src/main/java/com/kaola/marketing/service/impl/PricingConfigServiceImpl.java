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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
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

    private static final String PRODUCT_SERVICE_URL = "http://localhost:8085";

    private final MasseurLevelPricingMapper masseurLevelPricingMapper;
    private final TimeSlotPricingMapper timeSlotPricingMapper;
    private final StorePricingMapper storePricingMapper;
    private final RestTemplate restTemplate;

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
    @SuppressWarnings("unchecked")
    public PriceCalculationVO calculatePrice(PriceCalculationRequest request) {
        log.info("计算价格: projectId={}, masseurLevel={}, storeId={}, appointmentTime={}",
                request.getProjectId(), request.getMasseurLevel(), request.getStoreId(), request.getAppointmentTime());

        // 从 product-service 获取项目卖价底和划线原价底
        BigDecimal[] prices = getProjectPrices(request.getProjectId());
        BigDecimal basePrice = prices[0];         // 卖价底（project.basePrice）
        BigDecimal originalBasePrice = prices[1]; // 划线原价底（project.originalPrice）

        // 1. 获取技师等级系数
        BigDecimal levelMultiplier = getMasseurLevelMultiplier(request.getMasseurLevel());

        // 2. 获取时段系数
        BigDecimal timeSlotMultiplier = getTimeSlotMultiplier(request.getAppointmentTime());

        // 3. 获取门店系数
        BigDecimal storeMultiplier = getStoreMultiplier(request.getStoreId());

        // 4. 划线原价 = 项目 originalPrice × 全部系数
        BigDecimal originalPrice = originalBasePrice
                .multiply(levelMultiplier)
                .multiply(timeSlotMultiplier)
                .multiply(storeMultiplier)
                .setScale(0, BigDecimal.ROUND_FLOOR);

        // 5. 服务价格（卖价）= basePrice × 全部系数
        BigDecimal servicePrice = basePrice
                .multiply(levelMultiplier)
                .multiply(timeSlotMultiplier)
                .multiply(storeMultiplier)
                .setScale(0, BigDecimal.ROUND_FLOOR);

        // 6. 计算加钟费用
        BigDecimal extraPrice = BigDecimal.ZERO;
        if (request.getExtraMinutes() != null && request.getExtraMinutes() > 0) {
            BigDecimal pricePerMinute = basePrice.divide(new BigDecimal("60"), 4, BigDecimal.ROUND_FLOOR);
            extraPrice = pricePerMinute
                    .multiply(new BigDecimal(request.getExtraMinutes()))
                    .multiply(levelMultiplier)
                    .multiply(timeSlotMultiplier)
                    .multiply(storeMultiplier)
                    .setScale(0, BigDecimal.ROUND_FLOOR);
        }

        // 7. 计算优惠折扣（暂时不实现优惠券逻辑）
        BigDecimal discountAmount = BigDecimal.ZERO;

        // 8. 最终价格 = 服务价格 + 加钟费用 - 优惠折扣
        BigDecimal finalPrice = servicePrice
                .add(extraPrice)
                .subtract(discountAmount)
                .setScale(0, BigDecimal.ROUND_FLOOR);

        // 9. 构建返回结果
        PriceCalculationVO result = PriceCalculationVO.builder()
                .basePrice(basePrice)
                .levelMultiplier(levelMultiplier)
                .timeSlotMultiplier(timeSlotMultiplier)
                .storeMultiplier(storeMultiplier)
                .originalPrice(originalPrice)
                .servicePrice(servicePrice)
                .extraPrice(extraPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .build();

        log.info("价格计算完成: 基础价格={}, 服务价格={}, 加钟费用={}, 最终价格={}",
                basePrice, servicePrice, extraPrice, finalPrice);

        return result;
    }

    /**
     * 从 product-service 获取项目价格：[0]=卖价底(basePrice), [1]=划线原价底(originalPrice)
     */
    @SuppressWarnings("unchecked")
    private BigDecimal[] getProjectPrices(Long projectId) {
        if (projectId == null) {
            log.warn("projectId 为空，使用默认价格 0");
            return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO};
        }
        try {
            String url = PRODUCT_SERVICE_URL + "/project/" + projectId;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && Integer.valueOf(0).equals(response.get("code"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.get("price") != null) {
                    BigDecimal basePrice = new BigDecimal(data.get("price").toString());
                    // originalPrice 字段：若为空则与卖价相同
                    BigDecimal originalPrice = data.get("originalPrice") != null
                            ? new BigDecimal(data.get("originalPrice").toString())
                            : basePrice;
                    log.info("获取项目 {} 价格成功: basePrice={}, originalPrice={}", projectId, basePrice, originalPrice);
                    return new BigDecimal[]{basePrice, originalPrice};
                }
            }
            log.warn("获取项目 {} 价格失败，响应: {}", projectId, response);
        } catch (Exception e) {
            log.error("调用 product-service 获取项目 {} 价格失败", projectId, e);
        }
        log.error("无法获取项目 {} 价格，请检查 product-service 是否正常运行", projectId);
        return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO};
    }

    /**
     * 获取技师等级系数
     */
    private BigDecimal getMasseurLevelMultiplier(Integer level) {
        if (level == null) {
            return BigDecimal.ONE;
        }
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
        int dayOfWeek = appointmentTime.getDayOfWeek().getValue();
        String timeStr = String.format("%02d:%02d", appointmentTime.getHour(), appointmentTime.getMinute());

        List<TimeSlotPricing> timeSlotPricings = timeSlotPricingMapper.selectList(
                new LambdaQueryWrapper<TimeSlotPricing>()
                        .eq(TimeSlotPricing::getStatus, 1)
                        .orderByDesc(TimeSlotPricing::getSlotType)
        );

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
        // 1. 解析 dayOfWeek JSON：空数组表示适用所有星期
        String dayOfWeekJson = pricing.getDayOfWeek();
        if (dayOfWeekJson != null && !dayOfWeekJson.trim().isEmpty()) {
            JSONArray dayArray = JSON.parseArray(dayOfWeekJson);
            if (dayArray != null && !dayArray.isEmpty()) {
                boolean matchDay = false;
                for (Object d : dayArray) {
                    if (((Number) d).intValue() == dayOfWeek) {
                        matchDay = true;
                        break;
                    }
                }
                if (!matchDay) {
                    return false;
                }
            }
        }

        // 2. 解析 timeRanges JSON，检查时间是否在任意时段内
        String timeRangesJson = pricing.getTimeRanges();
        if (timeRangesJson == null || timeRangesJson.trim().isEmpty()) {
            return false;
        }

        JSONArray ranges = JSON.parseArray(timeRangesJson);
        if (ranges == null || ranges.isEmpty()) {
            return false;
        }

        LocalTime targetTime = LocalTime.parse(timeStr);
        for (Object rangeObj : ranges) {
            JSONObject range = (JSONObject) rangeObj;
            String startStr = range.getString("start");
            String endStr = range.getString("end");
            if (startStr == null || endStr == null) {
                continue;
            }

            LocalTime start = LocalTime.parse(startStr);
            LocalTime end = LocalTime.parse(endStr);

            boolean match;
            if (start.isBefore(end) || start.equals(end)) {
                // 普通时段：start < end（如 10:00~18:00）
                match = !targetTime.isBefore(start) && targetTime.isBefore(end);
            } else {
                // 跨夜时段：start > end（如 22:00~08:00）
                match = !targetTime.isBefore(start) || targetTime.isBefore(end);
            }

            if (match) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取门店系数
     */
    private BigDecimal getStoreMultiplier(Long storeId) {
        if (storeId == null) {
            return BigDecimal.ONE;
        }
        StorePricing pricing = storePricingMapper.selectOne(
                new LambdaQueryWrapper<StorePricing>()
                        .eq(StorePricing::getStoreId, storeId)
                        .eq(StorePricing::getStatus, 1)
        );
        return pricing != null ? pricing.getMultiplier() : BigDecimal.ONE;
    }
}
