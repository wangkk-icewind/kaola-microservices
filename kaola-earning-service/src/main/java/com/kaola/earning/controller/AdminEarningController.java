package com.kaola.earning.controller;

/**
 * TODO: 此Controller应该移至 masseur-service
 *
 * 原因: AdminEarningController 管理的是 MasseurEarning 实体，
 * 这个实体属于技师收益，应该在 kaola-masseur-service 中处理。
 *
 * 当前 earning-service 主要负责:
 * - Withdrawal (提现管理)
 * - 提现审核流程
 *
 * MasseurEarning 相关功能应该在 masseur-service 中实现。
 *
 * @author Kaola Team
 */

/*
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.Result;
import com.kaola.common.model.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "管理后台 - 收益管理", description = "收益流水的查询和统计接口")
@RestController
@RequestMapping("/admin/earning")
@RequiredArgsConstructor
public class AdminEarningController {

    // TODO: 移至 masseur-service 后，注入 MasseurEarningMapper
    // private final MasseurEarningMapper masseurEarningMapper;

    @Operation(summary = "分页查询收益流水列表", description = "支持技师ID、日期范围筛选")
    @GetMapping("/list")
    public Result<PageVO<MasseurEarning>> getEarningList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long masseurId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        log.info("分页查询收益流水列表, current: {}, pageSize: {}, masseurId: {}, startDate: {}, endDate: {}",
                 current, pageSize, masseurId, startDate, endDate);

        Page<MasseurEarning> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<MasseurEarning> wrapper = new LambdaQueryWrapper<>();

        if (masseurId != null) {
            wrapper.eq(MasseurEarning::getMasseurId, masseurId);
        }

        if (startDate != null && !startDate.isEmpty()) {
            try {
                wrapper.ge(MasseurEarning::getCreateTime, LocalDate.parse(startDate).atStartOfDay());
            } catch (Exception e) {
                log.warn("Invalid start date format: {}", startDate);
            }
        }

        if (endDate != null && !endDate.isEmpty()) {
            try {
                wrapper.le(MasseurEarning::getCreateTime, LocalDate.parse(endDate).atTime(23, 59, 59));
            } catch (Exception e) {
                log.warn("Invalid end date format: {}", endDate);
            }
        }

        wrapper.orderByDesc(MasseurEarning::getCreateTime);

        IPage<MasseurEarning> pageResult = masseurEarningMapper.selectPage(page, wrapper);
        PageVO<MasseurEarning> pageVO = PageVO.of(pageResult);

        return Result.success(pageVO);
    }

    @Operation(summary = "获取收益统计", description = "获取总订单金额、总佣金等统计数据")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getEarningStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        log.info("获取收益统计, startDate: {}, endDate: {}", startDate, endDate);

        LambdaQueryWrapper<MasseurEarning> wrapper = new LambdaQueryWrapper<>();

        if (startDate != null && !startDate.isEmpty()) {
            try {
                wrapper.ge(MasseurEarning::getCreateTime, LocalDate.parse(startDate).atStartOfDay());
            } catch (Exception e) {
                log.warn("Invalid start date format: {}", startDate);
            }
        }

        if (endDate != null && !endDate.isEmpty()) {
            try {
                wrapper.le(MasseurEarning::getCreateTime, LocalDate.parse(endDate).atTime(23, 59, 59));
            } catch (Exception e) {
                log.warn("Invalid end date format: {}", endDate);
            }
        }

        List<MasseurEarning> earnings = masseurEarningMapper.selectList(wrapper);

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalCount = earnings.size();

        for (MasseurEarning earning : earnings) {
            if (earning.getAmount() != null) {
                totalAmount = totalAmount.add(earning.getAmount());
            }
        }

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalAmount", totalAmount);
        statistics.put("totalCount", totalCount);

        return Result.success(statistics);
    }

    @Operation(summary = "根据技师查询收益", description = "查询指定技师的收益记录")
    @GetMapping("/byMasseur/{masseurId}")
    public Result<List<MasseurEarning>> getEarningsByMasseur(@PathVariable Long masseurId) {
        log.info("根据技师查询收益, masseurId: {}", masseurId);

        LambdaQueryWrapper<MasseurEarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MasseurEarning::getMasseurId, masseurId)
               .orderByDesc(MasseurEarning::getCreateTime);

        List<MasseurEarning> earnings = masseurEarningMapper.selectList(wrapper);
        return Result.success(earnings);
    }

    @Operation(summary = "获取收益详情", description = "根据ID获取收益详细信息")
    @GetMapping("/detail/{id}")
    public Result<MasseurEarning> getEarningDetail(@PathVariable Long id) {
        log.info("获取收益详情, id: {}", id);

        MasseurEarning earning = masseurEarningMapper.selectById(id);
        if (earning == null || earning.getDeleted() == 1) {
            return Result.error("收益记录不存在");
        }

        return Result.success(earning);
    }
}
*/
