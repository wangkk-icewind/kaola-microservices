package com.kaola.product.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kaola.product.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kaola.product.model.entity.TimeSlotPricing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kaola.product.service.TimeSlotPricingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * 管理后台 - 时段类型价格系数配置接口
 *
 * @author Kaola Team
 */

@Tag(name = "管理后台 - 定价配置", description = "时段类型价格系数管理接口")
@RestController
@RequestMapping("/admin/pricing/time-slot")
public class AdminTimeSlotPricingController {

    private static final Logger log = LoggerFactory.getLogger(AdminTimeSlotPricingController.class);

    private final TimeSlotPricingService timeSlotPricingService;

    public AdminTimeSlotPricingController(TimeSlotPricingService timeSlotPricingService) {
        this.timeSlotPricingService = timeSlotPricingService;
    }

    /**
     * 获取所有配置列表
     */
    @Operation(summary = "获取配置列表", description = "获取所有时段价格系数配置")
    @GetMapping("/list")
    public Result<List<TimeSlotPricing>> getList() {
        log.info("获取时段价格系数配置列表");
        List<TimeSlotPricing> list = timeSlotPricingService.getList();
        return Result.success(list);
    }

    /**
     * 获取配置详情
     */
    @Operation(summary = "获取配置详情", description = "根据ID获取配置详情")
    @GetMapping("/{id}")
    public Result<TimeSlotPricing> getDetail(@PathVariable Long id) {
        log.info("获取时段价格系数配置详情, id: {}", id);
        TimeSlotPricing pricing = timeSlotPricingService.getById(id);
        if (pricing == null) {
            return Result.error("配置不存在");
        }
        return Result.success(pricing);
    }

    /**
     * 创建配置
     */
    @Operation(summary = "创建配置", description = "新建时段价格系数配置")
    @PostMapping
    public Result<Boolean> create(@RequestBody TimeSlotPricing pricing) {
        log.info("创建时段价格系数配置, pricing: {}", pricing);
        try {
            boolean success = timeSlotPricingService.create(pricing);
            return success ? Result.success(true) : Result.error("创建失败");
        } catch (Exception e) {
            log.error("创建时段价格系数配置失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新配置
     */
    @Operation(summary = "更新配置", description = "修改时段价格系数配置")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody TimeSlotPricing pricing) {
        log.info("更新时段价格系数配置, id: {}, pricing: {}", id, pricing);
        try {
            boolean success = timeSlotPricingService.update(id, pricing);
            return success ? Result.success(true) : Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新时段价格系数配置失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量更新配置
     */
    @Operation(summary = "批量更新配置", description = "批量修改时段价格系数配置")
    @PostMapping("/batch")
    public Result<Boolean> batchUpdate(@RequestBody List<TimeSlotPricing> pricingList) {
        log.info("批量更新时段价格系数配置, count: {}", pricingList.size());
        try {
            boolean success = timeSlotPricingService.batchUpdate(pricingList);
            return success ? Result.success(true) : Result.error("批量更新失败");
        } catch (Exception e) {
            log.error("批量更新时段价格系数配置失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除配置
     */
    @Operation(summary = "删除配置", description = "删除时段价格系数配置")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        log.info("删除时段价格系数配置, id: {}", id);
        try {
            boolean success = timeSlotPricingService.delete(id);
            return success ? Result.success(true) : Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除时段价格系数配置失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新状态
     */
    @Operation(summary = "更新状态", description = "启用或禁用配置")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        log.info("更新时段价格系数配置状态, id: {}, status: {}", id, status);
        try {
            boolean success = timeSlotPricingService.updateStatus(id, status);
            return success ? Result.success(true) : Result.error("更新状态失败");
        } catch (Exception e) {
            log.error("更新时段价格系数配置状态失败", e);
            return Result.error(e.getMessage());
        }
    }
}
