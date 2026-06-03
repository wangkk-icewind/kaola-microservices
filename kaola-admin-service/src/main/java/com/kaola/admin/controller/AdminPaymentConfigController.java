package com.kaola.admin.controller;

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

/**
 * 支付参数配置管理（后台）。
 *
 * <p>配置存入 t_system_setting（group=payment），payment-service 通过内部接口读取并缓存。
 * 敏感字段（私钥、APIv3密钥）展示时掩码，提交时若值包含 **** 则跳过更新（保留原值）。
 */
@Slf4j
@Tag(name = "管理后台 - 支付配置管理")
@RestController
@RequestMapping("/admin/settings/payment")
@RequiredArgsConstructor
public class AdminPaymentConfigController {

    private static final String GROUP = "payment";

    /** 敏感字段：展示时掩码 */
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "wechat.apiV3Key", "wechat.privateKey", "wechat.privateKeyPath", "wechat.publicKey"
    );

    private final SystemSettingMapper settingMapper;

    @Operation(summary = "获取微信支付配置（敏感字段掩码）")
    @GetMapping
    public Result<Map<String, Object>> getPaymentConfig() {
        List<SystemSetting> list = settingMapper.findByGroup(GROUP);
        Map<String, Object> result = new LinkedHashMap<>();

        // 默认空值占位（方便前端渲染表单）
        for (String key : defaultKeys()) {
            result.put(key, "");
        }
        for (SystemSetting s : list) {
            String val = s.getSettingValue() == null ? "" : s.getSettingValue();
            if (SENSITIVE_KEYS.contains(s.getSettingKey()) && val.length() > 6) {
                val = val.substring(0, 3) + "****" + val.substring(val.length() - 3);
            }
            result.put(s.getSettingKey(), val);
        }
        return Result.success(result);
    }

    @Operation(summary = "保存微信支付配置", description = "若字段值包含 **** 视为掩码，不更新原值")
    @PostMapping
    public Result<Boolean> savePaymentConfig(@RequestBody Map<String, Object> config) {
        log.info("更新支付配置, keys={}", config.keySet());
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() == null ? "" : entry.getValue().toString().trim();

            // 掩码值跳过
            if (value.contains("****")) continue;

            SystemSetting existing = settingMapper.findByKey(key);
            if (existing != null) {
                existing.setSettingValue(value);
                settingMapper.updateById(existing);
            } else {
                SystemSetting s = new SystemSetting();
                s.setSettingKey(key);
                s.setSettingValue(value);
                s.setSettingGroup(GROUP);
                s.setDescription(keyDescription(key));
                settingMapper.insert(s);
            }
        }
        return Result.success(true);
    }

    // ── 内部接口（供 payment-service Feign 调用，绕过网关鉴权） ─────────────

    @Operation(summary = "[内部] 按分组返回配置（无掩码）", hidden = true)
    @GetMapping("/internal/group/{group}")
    public Map<String, String> getGroupConfigInternal(@PathVariable String group) {
        return settingMapper.findByGroup(group).stream()
                .filter(s -> s.getSettingValue() != null && !s.getSettingValue().isEmpty())
                .collect(Collectors.toMap(SystemSetting::getSettingKey, SystemSetting::getSettingValue));
    }

    private List<String> defaultKeys() {
        return List.of(
                "wechat.appId", "wechat.mchId", "wechat.mchSerialNo",
                "wechat.apiV3Key", "wechat.privateKey", "wechat.privateKeyPath", "wechat.notifyUrl",
                "wechat.publicKeyId", "wechat.publicKey"
        );
    }

    private String keyDescription(String key) {
        return switch (key) {
            case "wechat.appId" -> "小程序 AppID";
            case "wechat.mchId" -> "微信支付商户号";
            case "wechat.mchSerialNo" -> "商户 API 证书序列号";
            case "wechat.apiV3Key" -> "APIv3 密钥（32字节）";
            case "wechat.privateKey" -> "商户 API 私钥（PEM 内容）";
            case "wechat.privateKeyPath" -> "商户 API 私钥文件路径";
            case "wechat.notifyUrl" -> "支付回调通知 URL";
            case "wechat.publicKey" -> "微信支付公钥内容（PEM格式）";
            case "wechat.publicKeyId" -> "微信支付公钥ID（WechatPay-xxx格式）";
            default -> key;
        };
    }
}
