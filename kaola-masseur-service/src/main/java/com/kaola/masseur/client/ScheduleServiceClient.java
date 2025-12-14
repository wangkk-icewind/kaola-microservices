package com.kaola.masseur.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * 排班服务Feign客户端
 * 用于获取技师排班和可预约时间信息
 *
 * @author Kaola Team
 */
@FeignClient(name = "kaola-schedule-service")
public interface ScheduleServiceClient {

    /**
     * 获取技师可预约时间
     *
     * @param masseurId 技师ID
     * @param date      日期
     * @return 可预约时间段列表
     */
    @GetMapping("/schedule/available-time")
    Result<List<Object>> getAvailableTime(@RequestParam("masseurId") Long masseurId,
                                           @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date);

    /**
     * 获取技师排班列表
     *
     * @param masseurId 技师ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 排班列表
     */
    @GetMapping("/schedule/list")
    Result<List<Object>> getScheduleList(@RequestParam("masseurId") Long masseurId,
                                          @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                          @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate);
}
