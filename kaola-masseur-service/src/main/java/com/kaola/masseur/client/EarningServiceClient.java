package com.kaola.masseur.client;

import com.kaola.common.core.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * 收益服务Feign客户端
 * 用于获取收益统计、明细和处理提现
 *
 * @author Kaola Team
 */
@FeignClient(name = "kaola-earning-service", url = "http://localhost:8096")
public interface EarningServiceClient {

    /**
     * 获取技师收益统计
     *
     * @param masseurId 技师ID
     * @return 收益统计信息
     */
    @GetMapping("/earning/stats")
    Result<Object> getEarningStats(@RequestParam("masseurId") Long masseurId);

    /**
     * 获取收益明细列表
     *
     * @param masseurId 技师ID
     * @param month     月份（格式：yyyy-MM）
     * @return 收益明细列表
     */
    @GetMapping("/earning/list")
    Result<List<Object>> getEarningList(@RequestParam("masseurId") Long masseurId,
                                         @RequestParam(value = "month", required = false) String month);

    /**
     * 提现申请
     *
     * @param masseurId   技师ID
     * @param amount      提现金额
     * @param bankAccount 银行账户
     * @return 操作结果
     */
    @PostMapping("/earning/withdraw")
    Result<Boolean> withdraw(@RequestParam("masseurId") Long masseurId,
                              @RequestParam("amount") BigDecimal amount,
                              @RequestParam("bankAccount") String bankAccount);
}
