package com.kaola.admin.controller;

import com.kaola.admin.service.DashboardService;
import com.kaola.common.core.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 管理后台 - 仪表盘接口
 *
 * @author Kaola Team
 */
@Tag(name = "管理后台 - 仪表盘", description = "仪表盘数据统计接口")
@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private static final Logger log = LoggerFactory.getLogger(AdminDashboardController.class);

    private final DashboardService dashboardService;

    @Autowired
    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 获取仪表盘数据
     */
    @Operation(summary = "获取仪表盘数据", description = "获取首页数据统计")
    @GetMapping("/data")
    public Result<Map<String, Object>> getDashboardData() {
        log.info("获取仪表盘数据");
        Map<String, Object> data = dashboardService.getDashboardData();
        return Result.success(data);
    }
}
