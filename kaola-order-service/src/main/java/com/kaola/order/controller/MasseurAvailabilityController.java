package com.kaola.order.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.order.mapper.OrderMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 批量查询技师忙碌状态
     * 返回 Map&lt;masseurId, busy&gt;，true = 忙碌（该时段有重叠订单）
     */
    @Operation(summary = "批量查询技师忙碌状态",
               description = "检测指定技师列表在给定时间段内是否存在已有订单的时间重叠")
    @PostMapping("/availability")
    public Result<Map<Long, Boolean>> checkAvailability(@RequestBody AvailabilityRequest req) {
        if (req.getMasseurIds() == null || req.getMasseurIds().isEmpty() || req.getStartTime() == null) {
            return Result.error("参数不完整");
        }

        LocalDateTime dt = LocalDateTime.parse(req.getStartTime());
        LocalDate date = dt.toLocalDate();
        LocalTime time = dt.toLocalTime();
        int duration = req.getDuration() != null ? req.getDuration() : 60;

        Map<Long, Boolean> busyMap = new HashMap<>();
        for (Long masseurId : req.getMasseurIds()) {
            try {
                int conflicts = orderMapper.countMasseurOverlaps(masseurId, date, time, duration);
                busyMap.put(masseurId, conflicts > 0);
            } catch (Exception e) {
                log.warn("查询技师{}忙碌状态失败", masseurId, e);
                busyMap.put(masseurId, false); // 查询失败时保守处理，不误伤
            }
        }

        return Result.success(busyMap);
    }
}
