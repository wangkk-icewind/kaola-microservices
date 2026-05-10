package com.kaola.masseur.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.masseur.model.dto.MasseurDTO;
import com.kaola.masseur.model.vo.MasseurVO;
import com.kaola.masseur.service.MasseurService;
import com.kaola.masseur.service.MasseurProjectService;
// TODO: Cross-service dependency - LoginDTO should be in common-core
// import com.kaola.common.core.dto.LoginDTO;
// TODO: Cross-service dependency - LoginVO should be in common-core
// import com.kaola.common.core.vo.LoginVO;
// TODO: Cross-service dependency - TimeSlotVO should be in common-model or scheduling service
// import com.kaola.common.model.vo.TimeSlotVO;
// TODO: Cross-service dependency - EarningService should be handled via OpenFeign
// import com.kaola.masseur.service.EarningService;
// import com.kaola.masseur.model.vo.EarningStatsVO;
// import com.kaola.masseur.model.vo.EarningDetailVO;
// TODO: Cross-service dependency - ScheduleService should be handled via OpenFeign to scheduling service
// import com.kaola.masseur.service.ScheduleService;
// import com.kaola.masseur.model.vo.ScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 技师接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "技师接口", description = "技师登录、信息管理、收益、排班等接口")
@RestController
@RequestMapping("/masseur")
@RequiredArgsConstructor
@Validated
public class MasseurController {

    private final MasseurService masseurService;
    private final MasseurProjectService masseurProjectService;
    private final StringRedisTemplate redisTemplate;

    private static final String SMS_CODE_KEY_PREFIX = "sms:code:";
    private static final String MOCK_SMS_CODE = "123456";
    private static final long SMS_CODE_TTL_MINUTES = 5;
    // TODO: Cross-service dependency - EarningService should be accessed via OpenFeign from kaola-earning-service
    // private final EarningService earningService;
    // TODO: Cross-service dependency - ScheduleService should be accessed via OpenFeign from kaola-schedule-service
    // private final ScheduleService scheduleService;

    /**
     * 技师登录（开发模式：直接传入技师ID）
     * 生产环境应替换为微信授权登录流程
     */
    @Operation(summary = "技师登录", description = "开发模式：通过技师ID直接登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, Object> params) {
        Object masseurIdObj = params.get("masseurId");
        if (masseurIdObj == null) {
            return Result.error("masseurId不能为空");
        }
        Long masseurId = Long.valueOf(masseurIdObj.toString());
        MasseurVO masseurVO = masseurService.getMasseurInfo(masseurId);
        if (masseurVO == null) {
            return Result.error("技师不存在");
        }
        String token = "masseur_token_" + masseurId + "_" + System.currentTimeMillis();
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("token", token);
        result.put("masseurInfo", masseurVO);
        result.put("needBindPhone", false);
        return Result.success(result);
    }

    // TODO: Cross-service dependency - Login should be handled by auth service
    // /**
    //  * 技师微信登录
    //  *
    //  * @param loginDTO 登录请求参数
    //  * @return 登录结果
    //  */
    // @Operation(summary = "技师登录", description = "技师通过微信授权码进行登录")
    // @PostMapping("/login")
    // public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
    //     LoginVO loginVO = masseurService.login(loginDTO.getCode());
    //     return Result.success(loginVO);
    // }

    /**
     * 发送手机验证码（技师端）
     */
    @Operation(summary = "发送验证码", description = "向技师手机号发送短信验证码（测试环境固定为123456）")
    @PostMapping("/send-code")
    public Result<Boolean> sendCode(@RequestParam String phone) {
        redisTemplate.opsForValue().set(SMS_CODE_KEY_PREFIX + phone, MOCK_SMS_CODE, SMS_CODE_TTL_MINUTES, TimeUnit.MINUTES);
        log.info("技师端发送验证码, phone: {}, code: {}", phone, MOCK_SMS_CODE);
        return Result.success(true);
    }

    /**
     * 技师手机号+验证码登录
     */
    @Operation(summary = "手机号登录", description = "技师通过手机号和验证码登录")
    @PostMapping("/phone-login")
    public Result<Map<String, Object>> phoneLogin(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String code = params.get("code");
        if (phone == null || phone.isBlank() || code == null || code.isBlank()) {
            return Result.error("手机号和验证码不能为空");
        }
        String cachedCode = redisTemplate.opsForValue().get(SMS_CODE_KEY_PREFIX + phone);
        if (cachedCode == null) {
            return Result.error("验证码已过期，请重新获取");
        }
        if (!code.equals(cachedCode)) {
            return Result.error("验证码错误");
        }
        redisTemplate.delete(SMS_CODE_KEY_PREFIX + phone);

        MasseurVO masseurVO = masseurService.getMasseurByPhone(phone);
        if (masseurVO == null) {
            return Result.error("该手机号未注册技师账号");
        }

        String token = "masseur_token_" + masseurVO.getId() + "_" + System.currentTimeMillis();
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("token", token);
        result.put("masseurInfo", masseurVO);
        result.put("needBindPhone", false);
        log.info("技师手机号登录成功, phone: {}, masseurId: {}", phone, masseurVO.getId());
        return Result.success(result);
    }

    /**
     * 获取技师信息
     *
     * @param masseurId 技师ID
     * @return 技师信息
     */
    @Operation(summary = "获取技师信息", description = "获取当前登录技师的详细信息")
    @GetMapping("/info")
    public Result<MasseurVO> getMasseurInfo(
            @Parameter(description = "技师ID", hidden = true)
            @RequestHeader("X-Masseur-Id") Long masseurId) {
        MasseurVO masseurVO = masseurService.getMasseurInfo(masseurId);
        return Result.success(masseurVO);
    }

    /**
     * 更新技师信息
     *
     * @param masseurId 技师ID
     * @param dto       更新数据
     * @return 操作结果
     */
    @Operation(summary = "更新技师信息", description = "更新当前登录技师的信息")
    @PutMapping("/info")
    public Result<Boolean> updateMasseurInfo(
            @Parameter(description = "技师ID", hidden = true)
            @RequestHeader("X-Masseur-Id") Long masseurId,
            @Valid @RequestBody MasseurDTO dto) {
        boolean success = masseurService.updateMasseurInfo(masseurId, dto);
        return Result.success(success);
    }

    /**
     * 获取技师详情（公开接口）
     *
     * @param id 技师ID
     * @return 技师详情
     */
    @Operation(summary = "获取技师详情", description = "根据技师ID获取技师详细信息（公开接口）")
    @GetMapping("/detail/{id}")
    public Result<MasseurVO> getMasseurDetail(
            @Parameter(description = "技师ID", required = true)
            @PathVariable @NotNull(message = "技师ID不能为空") Long id) {
        MasseurVO masseurVO = masseurService.getMasseurInfo(id);
        return Result.success(masseurVO);
    }

    /**
     * 获取技师列表
     *
     * @param storeId   门店ID
     * @param symptomId 症状ID
     * @return 技师列表
     */
    @Operation(summary = "获取技师列表", description = "根据城市/门店/症状获取技师列表")
    @GetMapping("/list")
    public Result<List<MasseurVO>> getMasseurList(
            @Parameter(description = "城市名称")
            @RequestParam(required = false) String city,
            @Parameter(description = "门店ID")
            @RequestParam(required = false) Long storeId,
            @Parameter(description = "症状ID")
            @RequestParam(required = false) Long symptomId) {
        List<MasseurVO> masseurList;

        // 优先级：门店+症状 > 症状 > 门店 > 城市 > 全部
        if (storeId != null && symptomId != null) {
            masseurList = masseurService.getMasseursByStoreAndSymptom(storeId, symptomId);
        } else if (symptomId != null) {
            masseurList = masseurService.getMasseursBySymptom(symptomId);
        } else if (storeId != null) {
            masseurList = masseurService.getMasseursByStore(storeId);
        } else if (city != null && !city.isBlank()) {
            masseurList = masseurService.getMasseursByCity(city);
        } else {
            masseurList = masseurService.getMasseursByStore(null);
        }
        return Result.success(masseurList);
    }

    // TODO: Cross-service dependency - Available time should be fetched via OpenFeign from kaola-schedule-service
    // /**
    //  * 获取可预约时间
    //  *
    //  * @param masseurId 技师ID
    //  * @param date      日期
    //  * @return 可预约时间段列表
    //  */
    // @Operation(summary = "获取可预约时间", description = "获取指定技师在指定日期的可预约时间段")
    // @GetMapping("/available-time")
    // public Result<List<TimeSlotVO>> getAvailableTime(
    //         @Parameter(description = "技师ID", required = true)
    //         @RequestParam @NotNull(message = "技师ID不能为空") Long masseurId,
    //         @Parameter(description = "日期", required = true)
    //         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    //     List<TimeSlotVO> timeSlots = masseurService.getAvailableTime(masseurId, date);
    //     return Result.success(timeSlots);
    // }

    // TODO: Cross-service dependency - Earnings should be fetched via OpenFeign from kaola-earning-service
    // /**
    //  * 获取收益统计
    //  *
    //  * @param masseurId 技师ID
    //  * @return 收益统计信息
    //  */
    // @Operation(summary = "获取收益统计", description = "获取当前技师的收益统计信息")
    // @GetMapping("/earnings")
    // public Result<EarningStatsVO> getEarnings(
    //         @Parameter(description = "技师ID", hidden = true)
    //         @RequestHeader("X-Masseur-Id") Long masseurId) {
    //     EarningStatsVO earningStats = earningService.getEarningStats(masseurId);
    //     return Result.success(earningStats);
    // }

    // TODO: Cross-service dependency - Earnings should be fetched via OpenFeign from kaola-earning-service
    // /**
    //  * 获取收益明细
    //  *
    //  * @param masseurId 技师ID
    //  * @param month     月份
    //  * @return 收益明细列表
    //  */
    // @Operation(summary = "获取收益明细", description = "获取指定月份的收益明细列表")
    // @GetMapping("/earnings/list")
    // public Result<List<EarningDetailVO>> getEarningList(
    //         @Parameter(description = "技师ID", hidden = true)
    //         @RequestHeader("X-Masseur-Id") Long masseurId,
    //         @Parameter(description = "月份，格式：yyyy-MM")
    //         @RequestParam(required = false) String month) {
    //     List<EarningDetailVO> earningList = earningService.getEarningList(masseurId, month);
    //     return Result.success(earningList);
    // }

    // TODO: Cross-service dependency - Withdrawal should be handled via OpenFeign to kaola-earning-service
    // /**
    //  * 提现申请
    //  *
    //  * @param masseurId   技师ID
    //  * @param amount      提现金额
    //  * @param bankAccount 银行账户
    //  * @return 操作结果
    //  */
    // @Operation(summary = "提现申请", description = "技师发起提现申请")
    // @PostMapping("/withdraw")
    // public Result<Boolean> withdraw(
    //         @Parameter(description = "技师ID", hidden = true)
    //         @RequestHeader("X-Masseur-Id") Long masseurId,
    //         @Parameter(description = "提现金额", required = true)
    //         @RequestParam @NotNull(message = "提现金额不能为空") BigDecimal amount,
    //         @Parameter(description = "银行账户", required = true)
    //         @RequestParam @NotBlank(message = "银行账户不能为空") String bankAccount) {
    //     boolean success = earningService.withdraw(masseurId, amount, bankAccount);
    //     return Result.success(success);
    // }

    // TODO: Cross-service dependency - Schedule should be fetched via OpenFeign from kaola-schedule-service
    // /**
    //  * 获取排班
    //  *
    //  * @param masseurId 技师ID
    //  * @param startDate 开始日期
    //  * @param endDate   结束日期
    //  * @return 排班列表
    //  */
    // @Operation(summary = "获取排班", description = "获取技师在指定日期范围内的排班")
    // @GetMapping("/schedule")
    // public Result<List<ScheduleVO>> getSchedule(
    //         @Parameter(description = "技师ID", hidden = true)
    //         @RequestHeader("X-Masseur-Id") Long masseurId,
    //         @Parameter(description = "开始日期")
    //         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
    //         @Parameter(description = "结束日期")
    //         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
    //     // 默认查询未来7天
    //     if (startDate == null) {
    //         startDate = LocalDate.now();
    //     }
    //     if (endDate == null) {
    //         endDate = startDate.plusDays(7);
    //     }
    //     List<ScheduleVO> scheduleList = scheduleService.getScheduleList(masseurId, startDate, endDate);
    //     return Result.success(scheduleList);
    // }

    /**
     * 批量设置技师的项目列表
     *
     * @param masseurId  技师ID
     * @param projectIds 项目ID列表
     * @return 操作结果
     */
    @Operation(summary = "设置技师项目", description = "批量设置技师的擅长项目列表（后台管理使用）")
    @PostMapping("/{masseurId}/projects")
    public Result<Boolean> setMasseurProjects(
            @Parameter(description = "技师ID", required = true)
            @PathVariable @NotNull(message = "技师ID不能为空") Long masseurId,
            @Parameter(description = "项目ID列表", required = true)
            @RequestBody @NotNull(message = "项目ID列表不能为空") List<Long> projectIds) {
        masseurProjectService.setMasseurProjects(masseurId, projectIds);
        return Result.success(true);
    }

    /**
     * 获取技师的项目ID列表
     *
     * @param masseurId 技师ID
     * @return 项目ID列表
     */
    @Operation(summary = "获取技师项目", description = "获取指定技师的擅长项目ID列表")
    @GetMapping("/{masseurId}/projects")
    public Result<List<Long>> getMasseurProjects(
            @Parameter(description = "技师ID", required = true)
            @PathVariable @NotNull(message = "技师ID不能为空") Long masseurId) {
        List<Long> projectIds = masseurProjectService.getMasseurProjectIds(masseurId);
        return Result.success(projectIds);
    }

    /**
     * 删除技师的项目关联
     *
     * @param masseurId 技师ID
     * @param projectId 项目ID
     * @return 操作结果
     */
    @Operation(summary = "删除技师项目", description = "删除技师的项目关联")
    @DeleteMapping("/{masseurId}/projects/{projectId}")
    public Result<Boolean> removeMasseurProject(
            @Parameter(description = "技师ID", required = true)
            @PathVariable @NotNull(message = "技师ID不能为空") Long masseurId,
            @Parameter(description = "项目ID", required = true)
            @PathVariable @NotNull(message = "项目ID不能为空") Long projectId) {
        masseurProjectService.removeMasseurProject(masseurId, projectId);
        return Result.success(true);
    }
}
