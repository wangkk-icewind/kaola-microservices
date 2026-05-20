package com.kaola.order.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.order.mapper.OrderMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 技师可用性查询 - 批量忙碌状态检测
 * 路由：POST /api/masseur/availability → order-service /masseur/availability
 */
@Slf4j
@Tag(name = "技师可用性", description = "批量查询技师在指定时段的忙碌状态")
@RestController
@RequestMapping("/masseur")
@RequiredArgsConstructor
public class MasseurAvailabilityController {

    private final OrderMapper orderMapper;

    @Data
    public static class AvailabilityRequest {
        /** 技师ID列表 */
        private List<Long> masseurIds;
        /** 预约开始时间，ISO 8601 格式，如 "2026-05-10T18:00:00" */
        private String startTime;
        /** 服务时长（分钟），用于时间重叠检测窗口 */
        private Integer duration;
    }

    @Data
    public static class MasseurAvailabilityInfo {
        private boolean busy;
        private String freeAt;
    }

    /**
     * 批量查询技师忙碌状态
     * 返回 Map&lt;masseurId, { busy, freeAt }&gt;，freeAt 为 "HH:mm" 格式，不忙碌时为 null
     */
    @Operation(summary = "批量查询技师忙碌状态",
               description = "检测指定技师列表在给定时间段内是否存在已有订单的时间重叠，返回忙碌状态及预计空闲时间")
    @PostMapping("/availability")
    public Result<Map<Long, MasseurAvailabilityInfo>> checkAvailability(@RequestBody AvailabilityRequest req) {
        if (req.getMasseurIds() == null || req.getMasseurIds().isEmpty() || req.getStartTime() == null) {
            return Result.error("参数不完整");
        }

        LocalDateTime dt = LocalDateTime.parse(req.getStartTime());
        LocalDate date = dt.toLocalDate();
        LocalTime time = dt.toLocalTime();
        int duration = req.getDuration() != null ? req.getDuration() : 60;

        Map<Long, MasseurAvailabilityInfo> busyMap = new HashMap<>();
        for (Long masseurId : req.getMasseurIds()) {
            MasseurAvailabilityInfo info = new MasseurAvailabilityInfo();
            try {
                int conflicts = orderMapper.countMasseurOverlaps(masseurId, date, time, duration);
                info.setBusy(conflicts > 0);
                if (conflicts > 0) {
                    info.setFreeAt(orderMapper.getMasseurFreeAt(masseurId, date, time, duration));
                }
            } catch (Exception e) {
                log.warn("查询技师{}忙碌状态失败", masseurId, e);
                info.setBusy(false);
            }
            busyMap.put(masseurId, info);
        }

        return Result.success(busyMap);
    }

    @Data
    public static class SlotVO {
        private String startTime;   // "HH:mm"
        private int totalDuration;  // 分钟
    }

    /**
     * 批量获取技师当天的预约时间段列表（供前端计算空闲时间）
     * GET /masseur/daily-slots?masseurIds=1,2,3&date=2026-05-20
     * 返回 Map&lt;masseurId, [{ startTime, totalDuration }]&gt;
     */
    @Operation(summary = "获取技师日程时间段", description = "批量获取技师在指定日期的预约时间段列表，供前端计算最早空闲时间")
    @GetMapping("/daily-slots")
    public Result<Map<Long, List<SlotVO>>> getDailySlots(
            @RequestParam String masseurIds,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        List<Long> idList = java.util.Arrays.stream(masseurIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());

        Map<Long, List<SlotVO>> result = new HashMap<>();
        idList.forEach(id -> result.put(id, new ArrayList<>()));

        try {
            List<Map<String, Object>> rows = orderMapper.getMasseurDailySlots(idList, date);
            for (Map<String, Object> row : rows) {
                Long masseurId = ((Number) row.get("masseurId")).longValue();
                SlotVO slot = new SlotVO();
                slot.setStartTime((String) row.get("startTime"));
                slot.setTotalDuration(((Number) row.get("totalDuration")).intValue());
                result.computeIfAbsent(masseurId, k -> new ArrayList<>()).add(slot);
            }
        } catch (Exception e) {
            log.warn("获取技师日程时间段失败", e);
        }

        return Result.success(result);
    }
}
