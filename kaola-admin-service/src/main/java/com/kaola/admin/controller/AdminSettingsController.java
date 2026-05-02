package com.kaola.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.admin.mapper.SystemSettingMapper;
import com.kaola.admin.model.entity.SystemSetting;
import com.kaola.common.core.dto.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "管理后台 - 系统配置管理")
@RestController
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final SystemSettingMapper systemSettingMapper;

    @Operation(summary = "获取系统配置")
    @GetMapping("/get")
    public Result<Map<String, Object>> getSettings() {
        List<SystemSetting> all = systemSettingMapper.selectList(null);
        Map<String, Object> result = new LinkedHashMap<>();
        for (SystemSetting s : all) {
            result.put(s.getSettingKey(), s.getSettingValue());
        }
        // fallback hard-coded defaults if table empty
        result.putIfAbsent("siteName", "考拉运营管理系统");
        result.putIfAbsent("siteDescription", "高效、便捷的门店运营管理平台");
        result.putIfAbsent("contactPhone", "400-123-4567");
        result.putIfAbsent("contactEmail", "service@kaola.com");
        result.putIfAbsent("companyAddress", "北京市朝阳区建国路88号");
        result.putIfAbsent("orderCancelTime", 30);
        result.putIfAbsent("orderAutoCompleteTime", 24);
        result.putIfAbsent("commissionRate", 0.15);
        return Result.success(result);
    }

    @Operation(summary = "更新系统配置")
    @PostMapping("/update")
    public Result<Boolean> updateSettings(@RequestBody Map<String, Object> settings) {
        log.info("更新系统配置, keys: {}", settings.keySet());
        for (Map.Entry<String, Object> entry : settings.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() == null ? "" : entry.getValue().toString();
            SystemSetting existing = systemSettingMapper.findByKey(key);
            if (existing != null) {
                existing.setSettingValue(value);
                systemSettingMapper.updateById(existing);
            } else {
                SystemSetting newSetting = new SystemSetting();
                newSetting.setSettingKey(key);
                newSetting.setSettingValue(value);
                newSetting.setSettingGroup("general");
                systemSettingMapper.insert(newSetting);
            }
        }
        return Result.success(true);
    }

    @Operation(summary = "按分组获取配置")
    @GetMapping("/group/{group}")
    public Result<Map<String, String>> getByGroup(@PathVariable String group) {
        List<SystemSetting> list = systemSettingMapper.findByGroup(group);
        Map<String, String> result = list.stream()
                .collect(Collectors.toMap(SystemSetting::getSettingKey, s -> s.getSettingValue() == null ? "" : s.getSettingValue()));
        return Result.success(result);
    }

    @Operation(summary = "获取业务设置")
    @GetMapping("/business")
    public Result<Map<String, Object>> getBusinessSettings() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("orderCancelTime", getIntValue("orderCancelTime", 30));
        m.put("orderAutoCompleteTime", getIntValue("orderAutoCompleteTime", 24));
        m.put("commissionRate", getDoubleValue("commissionRate", 0.15));
        m.put("withdrawalMinAmount", getIntValue("withdrawalMinAmount", 100));
        m.put("withdrawalMaxAmount", getIntValue("withdrawalMaxAmount", 50000));
        m.put("appointmentAdvanceTime", getIntValue("appointmentAdvanceTime", 2));
        m.put("appointmentCancelTime", getIntValue("appointmentCancelTime", 6));
        return Result.success(m);
    }

    @Operation(summary = "更新业务设置")
    @PostMapping("/business/update")
    public Result<Boolean> updateBusinessSettings(@RequestBody Map<String, Object> settings) {
        return updateSettings(settings);
    }

    private int getIntValue(String key, int defaultVal) {
        SystemSetting s = systemSettingMapper.findByKey(key);
        if (s != null && s.getSettingValue() != null && !s.getSettingValue().isEmpty()) {
            try { return Integer.parseInt(s.getSettingValue()); } catch (Exception ignored) {}
        }
        return defaultVal;
    }

    private double getDoubleValue(String key, double defaultVal) {
        SystemSetting s = systemSettingMapper.findByKey(key);
        if (s != null && s.getSettingValue() != null && !s.getSettingValue().isEmpty()) {
            try { return Double.parseDouble(s.getSettingValue()); } catch (Exception ignored) {}
        }
        return defaultVal;
    }
}
