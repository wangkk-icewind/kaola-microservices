package com.kaola.masseur.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.common.model.vo.PageVO;
import com.kaola.masseur.model.entity.MasseurEarning;
import com.kaola.masseur.service.MasseurEarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 财务管理 - 收益流水接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "财务管理 - 收益流水", description = "收益流水查询接口")
@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
@Validated
public class FinanceController {

    private final MasseurEarningService masseurEarningService;

    /**
     * 分页查询收益流水列表
     */
    @Operation(summary = "分页查询收益流水列表", description = "支持技师ID、日期范围筛选")
    @GetMapping("/earnings")
    public Result<PageVO<MasseurEarning>> getEarnings(
            @Parameter(description = "当前页", example = "1")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "技师ID")
            @RequestParam(required = false) Long masseurId,
            @Parameter(description = "开始日期")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期")
            @RequestParam(required = false) String endDate) {

        log.info("分页查询收益流水列表, current: {}, pageSize: {}, masseurId: {}, startDate: {}, endDate: {}",
                current, pageSize, masseurId, startDate, endDate);

        PageVO<MasseurEarning> pageVO = masseurEarningService.getEarningsList(
                masseurId, startDate, endDate, current, pageSize);
        return Result.success(pageVO);
    }
}
