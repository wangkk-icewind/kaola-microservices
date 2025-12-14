package com.kaola.admin.controller;

import com.kaola.common.core.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理后台 - 系统配置管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "管理后台 - 系统配置管理", description = "系统配置的查询和修改接口")
@RestController
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    /**
     * 获取系统配置
     */
    @Operation(summary = "获取系统配置", description = "获取所有系统配置信息")
    @GetMapping("/get")
    public Result<Map<String, Object>> getSettings() {
        log.info("获取系统配置");

        Map<String, Object> settings = new HashMap<>();

        // 基础设置
        settings.put("siteName", "考拉运营管理系统");
        settings.put("siteDescription", "高效、便捷的门店运营管理平台");
        settings.put("contactPhone", "400-123-4567");
        settings.put("contactEmail", "service@kaola.com");
        settings.put("companyAddress", "北京市朝阳区建国路88号");

        // 业务设置
        settings.put("orderCancelTime", 30); // 订单自动取消时间（分钟）
        settings.put("orderAutoCompleteTime", 24); // 订单自动完成时间（小时）
        settings.put("commissionRate", 0.15); // 平台佣金比例
        settings.put("withdrawalMinAmount", 100); // 最小提现金额
        settings.put("withdrawalMaxAmount", 50000); // 最大提现金额

        return Result.success(settings);
    }

    /**
     * 更新系统配置
     */
    @Operation(summary = "更新系统配置", description = "更新系统配置信息")
    @PostMapping("/update")
    public Result<Boolean> updateSettings(@RequestBody Map<String, Object> settings) {
        log.info("更新系统配置, settings: {}", settings);

        // In a real application, you would save these settings to database
        // For now, just return success

        // Validate required fields
        if (settings.containsKey("siteName") &&
            (settings.get("siteName") == null || settings.get("siteName").toString().trim().isEmpty())) {
            return Result.error("网站名称不能为空");
        }

        if (settings.containsKey("contactPhone") &&
            (settings.get("contactPhone") == null || settings.get("contactPhone").toString().trim().isEmpty())) {
            return Result.error("联系电话不能为空");
        }

        if (settings.containsKey("contactEmail") &&
            (settings.get("contactEmail") == null || settings.get("contactEmail").toString().trim().isEmpty())) {
            return Result.error("联系邮箱不能为空");
        }

        // TODO: Save settings to database or configuration file

        return Result.success(true);
    }

    /**
     * 获取业务设置
     */
    @Operation(summary = "获取业务设置", description = "获取业务相关配置")
    @GetMapping("/business")
    public Result<Map<String, Object>> getBusinessSettings() {
        log.info("获取业务设置");

        Map<String, Object> businessSettings = new HashMap<>();
        businessSettings.put("orderCancelTime", 30);
        businessSettings.put("orderAutoCompleteTime", 24);
        businessSettings.put("commissionRate", 0.15);
        businessSettings.put("withdrawalMinAmount", 100);
        businessSettings.put("withdrawalMaxAmount", 50000);
        businessSettings.put("appointmentAdvanceTime", 2); // 预约提前时间（小时）
        businessSettings.put("appointmentCancelTime", 6); // 取消预约提前时间（小时）

        return Result.success(businessSettings);
    }

    /**
     * 更新业务设置
     */
    @Operation(summary = "更新业务设置", description = "更新业务相关配置")
    @PostMapping("/business/update")
    public Result<Boolean> updateBusinessSettings(@RequestBody Map<String, Object> settings) {
        log.info("更新业务设置, settings: {}", settings);

        // Validate business settings
        if (settings.containsKey("commissionRate")) {
            Double rate = Double.valueOf(settings.get("commissionRate").toString());
            if (rate < 0 || rate > 1) {
                return Result.error("佣金比例必须在0-1之间");
            }
        }

        if (settings.containsKey("withdrawalMinAmount")) {
            Integer minAmount = Integer.valueOf(settings.get("withdrawalMinAmount").toString());
            if (minAmount < 0) {
                return Result.error("最小提现金额不能为负数");
            }
        }

        // TODO: Save business settings to database or configuration file

        return Result.success(true);
    }
}
