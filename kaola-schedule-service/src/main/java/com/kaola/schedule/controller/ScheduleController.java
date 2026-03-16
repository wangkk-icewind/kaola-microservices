package com.kaola.schedule.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.Result;
import com.kaola.schedule.dto.PageVO;
import com.kaola.schedule.model.entity.Schedule;
import com.kaola.schedule.mapper.ScheduleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 排班管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "排班管理", description = "技师排班的增删改查接口")
@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleMapper scheduleRepository;

    /**
     * 分页查询排班列表
     */
    @Operation(summary = "分页查询排班列表", description = "支持技师ID、门店ID、日期筛选")
    @GetMapping("/list")
    public Result<PageVO<Schedule>> getScheduleList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long masseurId,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) LocalDate date) {

        log.info("分页查询排班列表, current: {}, pageSize: {}, masseurId: {}, storeId: {}, date: {}",
                 current, pageSize, masseurId, storeId, date);

        Page<Schedule> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Schedule::getDeleted, 0);

        if (masseurId != null) {
            wrapper.eq(Schedule::getMasseurId, masseurId);
        }

        if (storeId != null) {
            wrapper.eq(Schedule::getStoreId, storeId);
        }

        if (date != null) {
            wrapper.eq(Schedule::getDate, date);
        }

        wrapper.orderByDesc(Schedule::getDate)
               .orderByAsc(Schedule::getStartTime);

        IPage<Schedule> pageResult = scheduleRepository.selectPage(page, wrapper);

        // Convert IPage to PageVO
        PageVO<Schedule> pageVO = new PageVO<>();
        pageVO.setRecords(pageResult.getRecords());
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setCurrent(pageResult.getCurrent());
        pageVO.setPageSize(pageResult.getSize());
        pageVO.setPages(pageResult.getPages());

        return Result.success(pageVO);
    }

    /**
     * 根据日期查询排班
     */
    @Operation(summary = "根据日期查询排班", description = "查询指定日期的排班，可选门店筛选")
    @GetMapping("/byDate")
    public Result<List<Schedule>> getSchedulesByDate(
            @RequestParam LocalDate date,
            @RequestParam(required = false) Long storeId) {

        log.info("根据日期查询排班, date: {}, storeId: {}", date, storeId);

        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getDeleted, 0)
               .eq(Schedule::getDate, date);

        if (storeId != null) {
            wrapper.eq(Schedule::getStoreId, storeId);
        }

        wrapper.orderByAsc(Schedule::getStartTime);

        List<Schedule> schedules = scheduleRepository.selectList(wrapper);
        return Result.success(schedules);
    }

    /**
     * 创建单个排班
     */
    @Operation(summary = "创建单个排班", description = "新建技师排班记录")
    @PostMapping("/create")
    public Result<Boolean> createSchedule(@RequestBody Schedule schedule) {
        log.info("创建排班, masseurId: {}, storeId: {}, date: {}",
                 schedule.getMasseurId(), schedule.getStoreId(), schedule.getDate());

        if (schedule.getStatus() == null) {
            schedule.setStatus(1);
        }

        int rows = scheduleRepository.insert(schedule);
        return rows > 0 ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 批量创建排班
     */
    @Operation(summary = "批量创建排班", description = "批量新建技师排班记录")
    @PostMapping("/batchCreate")
    public Result<Boolean> batchCreateSchedules(@RequestBody List<Schedule> schedules) {
        log.info("批量创建排班, 数量: {}", schedules.size());

        if (schedules == null || schedules.isEmpty()) {
            return Result.error("排班列表不能为空");
        }

        for (Schedule schedule : schedules) {
            if (schedule.getStatus() == null) {
                schedule.setStatus(1);
            }
        }

        int rows = scheduleRepository.batchInsert(schedules);
        return rows > 0 ? Result.success(true) : Result.error("批量创建失败");
    }

    /**
     * 更新排班
     */
    @Operation(summary = "更新排班", description = "修改排班信息")
    @PutMapping("/update")
    public Result<Boolean> updateSchedule(@RequestBody Schedule schedule) {
        log.info("更新排班, id: {}, masseurId: {}, date: {}",
                 schedule.getId(), schedule.getMasseurId(), schedule.getDate());

        if (schedule.getId() == null) {
            return Result.error("排班ID不能为空");
        }

        Schedule existing = scheduleRepository.selectById(schedule.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("排班不存在");
        }

        int rows = scheduleRepository.updateById(schedule);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }

    // 技师端相关接口已移至 MasseurScheduleController

    /**
     * 技师端 - 获取月度排班（按日期分组）[已移至MasseurScheduleController，此处保留兼容]
     */
    @Operation(summary = "获取月度排班", description = "技师端：获取指定月份的排班，按日期分组")
    @GetMapping("/masseur/schedules/month/{year}/{month}")
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
     * 技师端 - 创建排班
     */
    @Operation(summary = "技师端创建排班")
    @PostMapping("/masseur/schedules")
    public Result<Boolean> createMasseurSchedule(
            @RequestHeader(value = "X-Masseur-Id", required = false) Long masseurId,
            @RequestBody Map<String, Object> params) {

        if (masseurId == null) return Result.error("未登录");

        Schedule schedule = new Schedule();
        schedule.setMasseurId(masseurId);
        if (params.get("storeId") != null) schedule.setStoreId(Long.valueOf(params.get("storeId").toString()));
        if (params.get("date") != null) schedule.setDate(LocalDate.parse(params.get("date").toString()));
        if (params.get("startTime") != null) schedule.setStartTime(java.time.LocalTime.parse(params.get("startTime").toString()));
        if (params.get("endTime") != null) schedule.setEndTime(java.time.LocalTime.parse(params.get("endTime").toString()));
        schedule.setStatus(1);

        int rows = scheduleRepository.insert(schedule);
        return rows > 0 ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 技师端 - 删除排班
     */
    @Operation(summary = "技师端删除排班")
    @DeleteMapping("/masseur/schedules/{id}")
    public Result<Boolean> deleteMasseurSchedule(
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
     * 技师端 - 检查排班冲突
     */
    @Operation(summary = "检查排班冲突")
    @PostMapping("/masseur/schedules/check-conflict")
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
     * 技师端 - 获取附近门店（返回全部门店）
     */
    @Operation(summary = "获取附近门店")
    @GetMapping("/masseur/schedules/nearby-stores")
    public Result<List<Map<String, Object>>> getNearbyStores(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false, defaultValue = "3000") Integer radius) {

        // 调用 store service — 此处简化为返回空列表，实际可通过 Feign 获取
        // 前端在门店加载失败时已有 mock 数据兜底
        return Result.success(new ArrayList<>());
    }

    /**
     * 删除排班（逻辑删除）
     */
    @Operation(summary = "删除排班", description = "逻辑删除排班")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteSchedule(@PathVariable Long id) {
        log.info("删除排班, id: {}", id);

        Schedule schedule = scheduleRepository.selectById(id);
        if (schedule == null) {
            return Result.error("排班不存在");
        }

        // MyBatis Plus自动处理逻辑删除(@TableLogic)
        int rows = scheduleRepository.deleteById(id);
        return rows > 0 ? Result.success(true) : Result.error("删除失败");
    }
}
