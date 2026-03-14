package com.kaola.product.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kaola.product.model.entity.TimeSlotPricing;
import com.kaola.product.repository.TimeSlotPricingRepository;
import com.kaola.product.service.TimeSlotPricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 时段类型价格系数配置服务实现类
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimeSlotPricingServiceImpl implements TimeSlotPricingService {

    private final TimeSlotPricingRepository timeSlotPricingRepository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public List<TimeSlotPricing> getList() {
        log.info("获取所有时段价格系数配置");
        return timeSlotPricingRepository.findAll();
    }

    @Override
    public List<TimeSlotPricing> getEnabledList() {
        log.info("获取所有启用的时段价格系数配置");
        return timeSlotPricingRepository.findAllEnabled();
    }

    @Override
    public TimeSlotPricing getById(Long id) {
        log.info("根据ID获取时段价格系数配置, id: {}", id);
        return timeSlotPricingRepository.selectById(id);
    }

    @Override
    public BigDecimal getMultiplierByDateTime(LocalDateTime dateTime) {
        log.info("根据时间获取价格系数, dateTime: {}", dateTime);

        List<TimeSlotPricing> pricingList = timeSlotPricingRepository.findAllEnabled();
        int dayOfWeek = dateTime.getDayOfWeek().getValue(); // 1-7 (Monday-Sunday)
        LocalTime time = dateTime.toLocalTime();

        // 按优先级排序：夜间 > 高峰 > 普通
        // 遍历所有配置，找到匹配的时段
        BigDecimal maxMultiplier = BigDecimal.ONE;

        for (TimeSlotPricing pricing : pricingList) {
            // 检查星期是否匹配
            if (!isDayOfWeekMatch(pricing.getDayOfWeek(), dayOfWeek)) {
                continue;
            }

            // 检查时间范围是否匹配
            if (isTimeRangeMatch(pricing.getTimeRanges(), time)) {
                // 找到匹配的时段，取最大的系数（如果有重叠）
                if (pricing.getMultiplier().compareTo(maxMultiplier) > 0) {
                    maxMultiplier = pricing.getMultiplier();
                }
            }
        }

        log.info("匹配到的价格系数: {}", maxMultiplier);
        return maxMultiplier;
    }

    /**
     * 检查星期是否匹配
     */
    private boolean isDayOfWeekMatch(String dayOfWeekJson, int dayOfWeek) {
        if (!StringUtils.hasText(dayOfWeekJson) || "[]".equals(dayOfWeekJson)) {
            // 空数组表示全部星期都适用
            return true;
        }

        try {
            JSONArray dayArray = JSON.parseArray(dayOfWeekJson);
            for (int i = 0; i < dayArray.size(); i++) {
                if (dayArray.getInteger(i) == dayOfWeek) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("解析星期配置失败: {}", dayOfWeekJson, e);
        }

        return false;
    }

    /**
     * 检查时间范围是否匹配
     */
    private boolean isTimeRangeMatch(String timeRangesJson, LocalTime time) {
        if (!StringUtils.hasText(timeRangesJson)) {
            return false;
        }

        try {
            JSONArray rangeArray = JSON.parseArray(timeRangesJson);
            for (int i = 0; i < rangeArray.size(); i++) {
                JSONObject range = rangeArray.getJSONObject(i);
                String startStr = range.getString("start");
                String endStr = range.getString("end");

                LocalTime start = LocalTime.parse(startStr, TIME_FORMATTER);
                LocalTime end = LocalTime.parse(endStr, TIME_FORMATTER);

                // 处理跨午夜的情况（如 22:00-08:00）
                if (end.isBefore(start)) {
                    // 分两段判断：start到23:59 和 00:00到end
                    if (time.compareTo(start) >= 0 || time.compareTo(end) <= 0) {
                        return true;
                    }
                } else {
                    // 正常情况
                    if (time.compareTo(start) >= 0 && time.compareTo(end) <= 0) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析时间范围配置失败: {}", timeRangesJson, e);
        }

        return false;
    }

    @Override
    @Transactional
    public boolean create(TimeSlotPricing pricing) {
        log.info("创建时段价格系数配置, pricing: {}", pricing);

        // 设置默认值
        if (pricing.getStatus() == null) {
            pricing.setStatus(1);
        }

        return timeSlotPricingRepository.insert(pricing) > 0;
    }

    @Override
    @Transactional
    public boolean update(Long id, TimeSlotPricing pricing) {
        log.info("更新时段价格系数配置, id: {}, pricing: {}", id, pricing);

        TimeSlotPricing existing = timeSlotPricingRepository.selectById(id);
        if (existing == null) {
            throw new RuntimeException("价格系数配置不存在");
        }

        pricing.setId(id);
        return timeSlotPricingRepository.updateById(pricing) > 0;
    }

    @Override
    @Transactional
    public boolean batchUpdate(List<TimeSlotPricing> pricingList) {
        log.info("批量更新时段价格系数配置, count: {}", pricingList.size());

        for (TimeSlotPricing pricing : pricingList) {
            if (pricing.getId() != null) {
                timeSlotPricingRepository.updateById(pricing);
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        log.info("删除时段价格系数配置, id: {}", id);
        return timeSlotPricingRepository.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        log.info("更新时段价格系数配置状态, id: {}, status: {}", id, status);

        TimeSlotPricing pricing = timeSlotPricingRepository.selectById(id);
        if (pricing == null) {
            throw new RuntimeException("价格系数配置不存在");
        }

        pricing.setStatus(status);
        return timeSlotPricingRepository.updateById(pricing) > 0;
    }
}
