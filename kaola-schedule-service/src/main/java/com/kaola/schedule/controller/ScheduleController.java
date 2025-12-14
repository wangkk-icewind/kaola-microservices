package com.kaola.schedule.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.PageVO;
import com.kaola.common.core.dto.Result;
import com.kaola.schedule.model.entity.Schedule;
import com.kaola.schedule.mapper.ScheduleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
