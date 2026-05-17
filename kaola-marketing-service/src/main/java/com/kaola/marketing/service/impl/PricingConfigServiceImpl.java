package com.kaola.marketing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.marketing.mapper.MasseurLevelPricingMapper;
import com.kaola.marketing.mapper.PromotionMapper;
import com.kaola.marketing.mapper.StorePricingMapper;
import com.kaola.marketing.mapper.TimeSlotPricingMapper;
import com.kaola.marketing.model.dto.PriceCalculationRequest;
import com.kaola.marketing.model.entity.MasseurLevelPricing;
import com.kaola.marketing.model.entity.Promotion;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final PromotionMapper promotionMapper;
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
        log.info("计算价格: projectId={}, masseurLevel={}, storeId={}, appointmentTime={}, isNewCustomer={}",
                request.getProjectId(), request.getMasseurLevel(), request.getStoreId(),
                request.getAppointmentTime(), request.getIsNewCustomer());

        // 从 product-service 获取项目卖价底、划线原价底、时长和加钟单价
        Object[] projectData = getProjectData(request.getProjectId());
        BigDecimal basePrice = (BigDecimal) projectData[0];
        BigDecimal originalBasePrice = (BigDecimal) projectData[1];
        Integer projectDuration = (Integer) projectData[2];
        BigDecimal projectExtraPPM = (BigDecimal) projectData[3]; // 项目设置的加钟单价（元/分钟，不含倍率）

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

        // 6. 计算加钟费用：优先使用项目配置的加钟单价，没有则按 basePrice/duration 推算
        BigDecimal extraPrice = BigDecimal.ZERO;
        if (request.getExtraMinutes() != null && request.getExtraMinutes() > 0) {
            int dur = projectDuration != null && projectDuration > 0 ? projectDuration : 60;
            BigDecimal basePPM = (projectExtraPPM != null && projectExtraPPM.compareTo(BigDecimal.ZERO) > 0)
                    ? projectExtraPPM
                    : basePrice.divide(new BigDecimal(dur), 4, BigDecimal.ROUND_FLOOR);
            extraPrice = basePPM
                    .multiply(new BigDecimal(request.getExtraMinutes()))
                    .multiply(levelMultiplier)
                    .multiply(timeSlotMultiplier)
                    .multiply(storeMultiplier)
                    .setScale(0, BigDecimal.ROUND_CEILING);
        }

        // 7. 查询并应用促销折扣
        PromotionResult promoResult = applyBestPromotion(servicePrice, request.getStoreId(),
                request.getIsNewCustomer(), request.getAppointmentTime());
        BigDecimal discountAmount = promoResult.discountAmount;
        String promotionName = promoResult.promotionName;
        Integer promotionType = promoResult.promotionType;

        // 8. 最终价格 = 服务价格 + 加钟费用 - 优惠折扣
        BigDecimal finalPrice = servicePrice
                .add(extraPrice)
                .subtract(discountAmount)
                .max(BigDecimal.ZERO)
                .setScale(0, BigDecimal.ROUND_FLOOR);

        // 9. 加钟基础单价（元/分钟，不含倍率）：优先项目配置值，否则按 basePrice/duration 推算
        int dur = projectDuration != null && projectDuration > 0 ? projectDuration : 60;
        BigDecimal extraPricePerMinute = (projectExtraPPM != null && projectExtraPPM.compareTo(BigDecimal.ZERO) > 0)
                ? projectExtraPPM
                : basePrice.divide(new BigDecimal(dur), 4, BigDecimal.ROUND_FLOOR);

        // 10. 构建返回结果
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
                .promotionName(promotionName)
                .promotionType(promotionType)
                .extraPricePerMinute(extraPricePerMinute)
                .duration(projectDuration)
                .build();

        log.info("价格计算完成: 基础价格={}, 服务价格={}, 加钟费用={}, 折扣={}/{}, 最终价格={}",
                basePrice, servicePrice, extraPrice, discountAmount, promotionName, finalPrice);

        return result;
    }

    /** 促销计算结果内部类 */
    private static class PromotionResult {
        BigDecimal discountAmount = BigDecimal.ZERO;
        String promotionName = null;
        Integer promotionType = null;
    }

    /**
     * 查询当前适用的最优促销并返回折扣金额、名称、类型。
     * 优先级：新客专属 > 普通折扣/满减；相同类别取折扣金额最大者。
     */
    private PromotionResult applyBestPromotion(BigDecimal servicePrice, Long storeId,
                                                Boolean isNewCustomer, LocalDateTime appointmentTime) {
        PromotionResult best = new PromotionResult();
        if (servicePrice == null || servicePrice.compareTo(BigDecimal.ZERO) <= 0) return best;

        LocalDateTime now = LocalDateTime.now();
        // 查询：全局促销（storeId IS NULL）或本门店促销
        List<Promotion> promotions = promotionMapper.selectList(
                new LambdaQueryWrapper<Promotion>()
                        .eq(Promotion::getStatus, 1)
                        .le(Promotion::getStartTime, now)
                        .ge(Promotion::getEndTime, now)
                        .and(w -> w.isNull(Promotion::getStoreId).or().eq(Promotion::getStoreId, storeId))
        );

        for (Promotion promo : promotions) {
            JSONObject rules;
            try {
                rules = promo.getRules() != null ? JSON.parseObject(promo.getRules()) : new JSONObject();
            } catch (Exception e) {
                continue;
            }
            int type = promo.getType() != null ? promo.getType() : 0;
            boolean isNewCustomerPromo = (type == 4 || type == 5);

            // 新客活动：isNewCustomer 必须为 true
            if (isNewCustomerPromo && !Boolean.TRUE.equals(isNewCustomer)) continue;

            // store_discount：按 triggerType 校验时段/提前预约条件
            if ("store_discount".equals(promo.getCategory())) {
                String triggerType = rules.getString("triggerType");
                if (triggerType == null) triggerType = "time_slot";

                if ("time_slot".equals(triggerType)) {
                    // 检查预约时间是否在 timeSlotStart~timeSlotEnd 时段内
                    if (!isAppointmentInTimeSlot(appointmentTime, rules)) continue;
                } else if ("advance_booking".equals(triggerType)) {
                    // 检查是否提前足够时间预约（当前时间距预约时间 >= advanceMinutes）
                    int advanceMins = rules.getIntValue("advanceMinutes");
                    if (advanceMins <= 0) advanceMins = 30;
                    long minutesAhead = java.time.Duration.between(now, appointmentTime).toMinutes();
                    if (minutesAhead < advanceMins) continue;
                }
                // off_peak 不需要额外时段检查，活动有效期内即可
            }

            BigDecimal minAmount = rules.getBigDecimal("minAmount");
            if (minAmount != null && minAmount.compareTo(BigDecimal.ZERO) > 0
                    && servicePrice.compareTo(minAmount) < 0) continue;

            BigDecimal discount = calcDiscountAmount(type, servicePrice, rules);
            if (discount != null && discount.compareTo(best.discountAmount) > 0) {
                best.discountAmount = discount;
                best.promotionName = promo.getName();
                best.promotionType = type;
            }
        }
        return best;
    }

    /** 根据促销类型和规则计算折扣金额 */
    private BigDecimal calcDiscountAmount(int type, BigDecimal servicePrice, JSONObject rules) {
        switch (type) {
            case 1: // 满减
            case 4: { // 新客立减
                BigDecimal reduce = rules.getBigDecimal("reduce");
                if (reduce == null) reduce = rules.getBigDecimal("discount");
                if (reduce != null && reduce.compareTo(BigDecimal.ZERO) > 0)
                    return reduce.min(servicePrice);
                break;
            }
            case 2: // 折扣
            case 5: { // 新客折扣
                BigDecimal discountRate = rules.getBigDecimal("discount");
                if (discountRate != null && discountRate.compareTo(BigDecimal.ONE) < 0
                        && discountRate.compareTo(BigDecimal.ZERO) > 0) {
                    return servicePrice.multiply(BigDecimal.ONE.subtract(discountRate))
                            .setScale(0, BigDecimal.ROUND_FLOOR);
                }
                break;
            }
            default:
                break;
        }
        return BigDecimal.ZERO;
    }

    /**
     * 检查预约时间是否落在门店折扣的时段窗口内（timeSlotStart ~ timeSlotEnd，左闭右开）。
     * 若 rules 中缺少时段字段则视为不满足条件（保守策略，不给折扣）。
     */
    private boolean isAppointmentInTimeSlot(LocalDateTime appointmentTime, JSONObject rules) {
        String startStr = rules.getString("timeSlotStart");
        String endStr = rules.getString("timeSlotEnd");
        if (startStr == null || endStr == null) return false;
        try {
            LocalTime target = LocalTime.of(appointmentTime.getHour(), appointmentTime.getMinute());
            LocalTime start = LocalTime.parse(startStr);
            LocalTime end = LocalTime.parse(endStr);
            if (start.isBefore(end) || start.equals(end)) {
                // 普通时段（如 10:00~11:30）：左闭右开
                return !target.isBefore(start) && target.isBefore(end);
            } else {
                // 跨夜时段（如 22:00~02:00）
                return !target.isBefore(start) || target.isBefore(end);
            }
        } catch (Exception e) {
            log.warn("门店折扣时段解析失败: start={}, end={}", startStr, endStr, e);
            return false;
        }
    }

    /**
     * 从 product-service 获取项目数据：[0]=卖价底(BigDecimal), [1]=划线原价底(BigDecimal), [2]=时长(Integer/null)
     */
    @SuppressWarnings("unchecked")
    private Object[] getProjectData(Long projectId) {
        if (projectId == null) {
            log.warn("projectId 为空，使用默认价格 0");
            return new Object[]{BigDecimal.ZERO, BigDecimal.ZERO, null, null};
        }
        try {
            String url = PRODUCT_SERVICE_URL + "/project/" + projectId;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && Integer.valueOf(0).equals(response.get("code"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.get("price") != null) {
                    BigDecimal basePrice = new BigDecimal(data.get("price").toString());
                    BigDecimal originalPrice = data.get("originalPrice") != null
                            ? new BigDecimal(data.get("originalPrice").toString())
                            : basePrice;
                    Integer duration = data.get("duration") != null
                            ? ((Number) data.get("duration")).intValue()
                            : null;
                    BigDecimal extraPPM = data.get("extraPricePerMinute") != null
                            ? new BigDecimal(data.get("extraPricePerMinute").toString())
                            : null;
                    log.info("获取项目 {} 数据成功: basePrice={}, originalPrice={}, duration={}, extraPPM={}",
                            projectId, basePrice, originalPrice, duration, extraPPM);
                    return new Object[]{basePrice, originalPrice, duration, extraPPM};
                }
            }
            log.warn("获取项目 {} 数据失败，响应: {}", projectId, response);
        } catch (Exception e) {
            log.error("调用 product-service 获取项目 {} 数据失败", projectId, e);
        }
        log.error("无法获取项目 {} 价格，请检查 product-service 是否正常运行", projectId);
        return new Object[]{BigDecimal.ZERO, BigDecimal.ZERO, null, null};
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
