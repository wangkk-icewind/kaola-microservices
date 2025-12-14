package com.kaola.earning.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.Result;
import com.kaola.earning.model.entity.Withdrawal;
import com.kaola.common.core.dto.PageVO;
import com.kaola.earning.mapper.WithdrawalMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理后台 - 提现管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "管理后台 - 提现管理", description = "技师提现申请的审批和管理接口")
@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
public class AdminWithdrawalController {

    private final WithdrawalMapper withdrawalRepository;

    /**
     * 分页查询提现列表
     */
    @Operation(summary = "分页查询提现列表", description = "支持技师ID、状态筛选")
    @GetMapping("/withdrawals")
    public Result<PageVO<Withdrawal>> getWithdrawalList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long masseurId,
            @RequestParam(required = false) Integer status) {

        log.info("分页查询提现列表, current: {}, pageSize: {}, masseurId: {}, status: {}",
                 current, pageSize, masseurId, status);

        Page<Withdrawal> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Withdrawal> wrapper = new LambdaQueryWrapper<>();

        if (masseurId != null) {
            wrapper.eq(Withdrawal::getMasseurId, masseurId);
        }

        if (status != null) {
            wrapper.eq(Withdrawal::getStatus, status);
        }

        wrapper.orderByDesc(Withdrawal::getCreateTime);

        IPage<Withdrawal> pageResult = withdrawalRepository.selectPage(page, wrapper);

        PageVO<Withdrawal> pageVO = new PageVO<>();
        pageVO.setRecords(pageResult.getRecords());
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setCurrent(pageResult.getCurrent());
        pageVO.setPageSize(pageResult.getSize());
        pageVO.setPages(pageResult.getPages());

        return Result.success(pageVO);
    }

    /**
     * 获取提现详情
     */
    @Operation(summary = "获取提现详情", description = "根据ID获取提现详细信息")
    @GetMapping("/detail/{id}")
    public Result<Withdrawal> getWithdrawalDetail(@PathVariable Long id) {
        log.info("获取提现详情, id: {}", id);

        Withdrawal withdrawal = withdrawalRepository.selectById(id);
        if (withdrawal == null || withdrawal.getDeleted() == 1) {
            return Result.error("提现记录不存在");
        }

        return Result.success(withdrawal);
    }

    /**
     * 审批提现申请
     */
    @Operation(summary = "审批提现申请", description = "批准或拒绝技师的提现申请")
    @PostMapping("/approve")
    public Result<Boolean> approveWithdrawal(@RequestBody Withdrawal request) {
        return processApproval(request);
    }

    /**
     * 审批提现申请 (前端兼容接口)
     */
    @Operation(summary = "审批提现申请", description = "批准或拒绝技师的提现申请")
    @PostMapping("/approveWithdrawal")
    public Result<Boolean> approveWithdrawalCompat(@RequestBody Withdrawal request) {
        return processApproval(request);
    }

    /**
     * 处理提现审批逻辑
     */
    private Result<Boolean> processApproval(Withdrawal request) {
        log.info("审批提现申请, id: {}, status: {}",
                 request.getId(), request.getStatus());

        if (request.getId() == null) {
            return Result.error("提现申请ID不能为空");
        }

        if (request.getStatus() == null) {
            return Result.error("审批状态不能为空");
        }

        // Status should be either 1 (approved) or 2 (rejected)
        if (request.getStatus() != 1 && request.getStatus() != 2) {
            return Result.error("审批状态无效");
        }

        Withdrawal existing = withdrawalRepository.selectById(request.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("提现申请不存在");
        }

        if (existing.getStatus() != 0) {
            return Result.error("该提现申请已审批，无法重复审批");
        }

        existing.setStatus(request.getStatus());
        existing.setProcessTime(LocalDateTime.now());
        existing.setUpdateTime(LocalDateTime.now());

        int rows = withdrawalRepository.updateById(existing);
        return rows > 0 ? Result.success(true) : Result.error("审批失败");
    }

    /**
     * 完成提现
     */
    @Operation(summary = "完成提现", description = "标记提现为已完成（已打款）")
    @PostMapping("/complete/{id}")
    public Result<Boolean> completeWithdrawal(@PathVariable Long id) {
        log.info("完成提现, id: {}", id);

        Withdrawal withdrawal = withdrawalRepository.selectById(id);
        if (withdrawal == null || withdrawal.getDeleted() == 1) {
            return Result.error("提现记录不存在");
        }

        if (withdrawal.getStatus() != 1) {
            return Result.error("只能完成已批准的提现申请");
        }

        withdrawal.setStatus(3); // 3 = completed
        int rows = withdrawalRepository.updateById(withdrawal);

        return rows > 0 ? Result.success(true) : Result.error("操作失败");
    }
}
