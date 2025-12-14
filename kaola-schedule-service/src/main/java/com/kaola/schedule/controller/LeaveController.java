package com.kaola.schedule.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.PageVO;
import com.kaola.common.core.dto.Result;
import com.kaola.schedule.model.entity.LeaveRequest;
import com.kaola.schedule.mapper.LeaveRequestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 请假管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "请假管理", description = "技师请假申请的审批和管理接口")
@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveRequestMapper leaveRequestRepository;

    /**
     * 分页查询请假列表
     */
    @Operation(summary = "分页查询请假列表", description = "支持技师ID、状态筛选")
    @GetMapping("/list")
    public Result<PageVO<LeaveRequest>> getLeaveList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long masseurId,
            @RequestParam(required = false) Integer status) {

        log.info("分页查询请假列表, current: {}, pageSize: {}, masseurId: {}, status: {}",
                 current, pageSize, masseurId, status);

        Page<LeaveRequest> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();

        // deleted条件由@TableLogic自动处理

        if (masseurId != null) {
            wrapper.eq(LeaveRequest::getMasseurId, masseurId);
        }

        if (status != null) {
            wrapper.eq(LeaveRequest::getStatus, status);
        }

        wrapper.orderByDesc(LeaveRequest::getId);

        IPage<LeaveRequest> pageResult = leaveRequestRepository.selectPage(page, wrapper);

        // Convert IPage to PageVO
        PageVO<LeaveRequest> pageVO = new PageVO<>();
        pageVO.setRecords(pageResult.getRecords());
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setCurrent(pageResult.getCurrent());
        pageVO.setPageSize(pageResult.getSize());
        pageVO.setPages(pageResult.getPages());

        return Result.success(pageVO);
    }

    /**
     * 审批请假申请
     */
    @Operation(summary = "审批请假申请", description = "批准或拒绝技师的请假申请")
    @PostMapping("/approve")
    public Result<Boolean> approveLeave(@RequestBody LeaveRequest request) {
        log.info("审批请假申请, id: {}, status: {}, remark: {}",
                 request.getId(), request.getStatus(), request.getRemark());

        if (request.getId() == null) {
            return Result.error("请假申请ID不能为空");
        }

        if (request.getStatus() == null) {
            return Result.error("审批状态不能为空");
        }

        LeaveRequest existing = leaveRequestRepository.selectById(request.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("请假申请不存在");
        }

        if (existing.getStatus() != 0) {
            return Result.error("该请假申请已审批，无法重复审批");
        }

        existing.setStatus(request.getStatus());
        existing.setRemark(request.getRemark());

        int rows = leaveRequestRepository.updateById(existing);
        return rows > 0 ? Result.success(true) : Result.error("审批失败");
    }
}
