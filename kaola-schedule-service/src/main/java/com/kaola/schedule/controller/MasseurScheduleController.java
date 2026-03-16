package com.kaola.schedule.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.common.core.dto.Result;
import com.kaola.schedule.mapper.ScheduleMapper;
import com.kaola.schedule.model.entity.Schedule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 技师端 - 排班接口
 * 路径: /masseur/schedules/**
 * 网关路由: /api/masseur/schedules/** → /masseur/schedules/**
 */
@Slf4j
@Tag(name = "技师端 - 排班", description = "技师查看和管理自己排班的接口")
@RestController
@RequestMapping("/masseur/schedules")
@RequiredArgsConstructor
public class MasseurScheduleController {

    private final ScheduleMapper scheduleRepository;

    /**
     * 获取月度排班（按日期分组）
     */
    @Operation(summary = "获取月度排班")
    @GetMapping("/month/{year}/{month}")
    public Result<List<Map<String, Object>>> getMonthSchedule(
            @PathVariable int year,
            @PathVariable int month,
            @RequestHeader(value = "X-Masseur-Id", required = false) Long masseurId) {

        log.info("获取月度排班, year: {}, month: {}, masseurId: {}", year, month, masseurId);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getDeleted, 0)
               .ge(Schedule::getDate, startDate)
               .le(Schedule::getDate, endDate);
        if (masseurId != null) {
            wrapper.eq(Schedule::getMasseurId, masseurId);
        }
        wrapper.orderByAsc(Schedule::getDate, Schedule::getStartTime);

        List<Schedule> schedules = scheduleRepository.selectList(wrapper);

        // 按日期分组
        Map<LocalDate, List<Schedule>> grouped = schedules.stream()
                .collect(Collectors.groupingBy(Schedule::getDate));

        List<Map<String, Object>> result = new ArrayList<>();
        grouped.forEach((date, daySchedules) -> {
            Map<String, Object> dayMap = new LinkedHashMap<>();
            dayMap.put("date", date.toString());
            dayMap.put("hasOrder", false);

            List<Map<String, Object>> shifts = daySchedules.stream().map(s -> {
                Map<String, Object> shift = new LinkedHashMap<>();
                shift.put("id", s.getId());
                shift.put("startTime", s.getStartTime() != null ? s.getStartTime().toString().substring(0, 5) : "");
                shift.put("endTime", s.getEndTime() != null ? s.getEndTime().toString().substring(0, 5) : "");
                shift.put("storeId", s.getStoreId());
                shift.put("storeName", "");
                return shift;
            }).collect(Collectors.toList());

            dayMap.put("shifts", shifts);
            result.add(dayMap);
        });

        result.sort(Comparator.comparing(m -> m.get("date").toString()));
        return Result.success(result);
    }

    /**
     * 创建排班
     */
    @Operation(summary = "技师端创建排班")
    @PostMapping
    public Result<Boolean> createSchedule(
            @RequestHeader(value = "X-Masseur-Id", required = false) Long masseurId,
            @RequestBody Map<String, Object> params) {

        if (masseurId == null) return Result.error("未登录");

        Schedule schedule = new Schedule();
        schedule.setMasseurId(masseurId);
        if (params.get("storeId") != null) schedule.setStoreId(Long.valueOf(params.get("storeId").toString()));
        if (params.get("date") != null) schedule.setDate(LocalDate.parse(params.get("date").toString()));
        if (params.get("startTime") != null) schedule.setStartTime(LocalTime.parse(params.get("startTime").toString()));
        if (params.get("endTime") != null) schedule.setEndTime(LocalTime.parse(params.get("endTime").toString()));
        schedule.setStatus(1);

        int rows = scheduleRepository.insert(schedule);
        return rows > 0 ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 删除排班
     */
    @Operation(summary = "技师端删除排班")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteSchedule(
            @RequestHeader(value = "X-Masseur-Id", required = false) Long masseurId,
            @PathVariable Long id) {

        if (masseurId == null) return Result.error("未登录");
        Schedule schedule = scheduleRepository.selectById(id);
        if (schedule == null) return Result.error("排班不存在");
        if (!masseurId.equals(schedule.getMasseurId())) return Result.error("无权限");
        int rows = scheduleRepository.deleteById(id);
        return rows > 0 ? Result.success(true) : Result.error("删除失败");
    }

    /**
     * 检查排班冲突
     */
    @Operation(summary = "检查排班冲突")
    @PostMapping("/check-conflict")
    public Result<Map<String, Object>> checkConflict(
            @RequestHeader(value = "X-Masseur-Id", required = false) Long masseurId,
            @RequestBody Map<String, Object> params) {

        Map<String, Object> result = new HashMap<>();
        if (masseurId == null || params.get("date") == null) {
            result.put("hasConflict", false);
            return Result.success(result);
        }

        LocalDate date = LocalDate.parse(params.get("date").toString());
        String startTime = params.get("startTime") != null ? params.get("startTime").toString() : "00:00";
        String endTime = params.get("endTime") != null ? params.get("endTime").toString() : "23:59";

        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getMasseurId, masseurId)
               .eq(Schedule::getDate, date)
               .eq(Schedule::getDeleted, 0);
        List<Schedule> existing = scheduleRepository.selectList(wrapper);

        boolean conflict = false;
        for (Schedule s : existing) {
            String eStart = s.getStartTime() != null ? s.getStartTime().toString().substring(0, 5) : "00:00";
            String eEnd = s.getEndTime() != null ? s.getEndTime().toString().substring(0, 5) : "23:59";
            if (startTime.compareTo(eEnd) < 0 && endTime.compareTo(eStart) > 0) {
                conflict = true;
                break;
            }
        }

        result.put("hasConflict", conflict);
        if (conflict) result.put("message", "该时间段已有排班");
        return Result.success(result);
    }

    /**
     * 获取附近门店（开发模式：返回空列表，前端使用 mock 数据兜底）
     */
    @Operation(summary = "获取附近门店")
    @GetMapping("/nearby-stores")
    public Result<List<Map<String, Object>>> getNearbyStores(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false, defaultValue = "3000") Integer radius) {

        return Result.success(new ArrayList<>());
    }
}
