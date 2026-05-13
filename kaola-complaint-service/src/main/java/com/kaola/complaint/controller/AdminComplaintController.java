package com.kaola.complaint.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.Result;
import com.kaola.complaint.model.entity.Complaint;
import com.kaola.complaint.dto.PageVO;
import com.kaola.complaint.mapper.ComplaintMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理后台 - 投诉管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "管理后台 - 投诉管理", description = "投诉的查询和处理接口")
@RestController
@RequestMapping("/admin/complaint")
@RequiredArgsConstructor
public class AdminComplaintController {

    private final ComplaintMapper complaintRepository;

    /**
     * 分页查询投诉列表
     */
    @Operation(summary = "分页查询投诉列表", description = "支持投诉类型、状态筛选")
    @GetMapping("/list")
    public Result<PageVO<Complaint>> getComplaintList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {

        log.info("分页查询投诉列表, current: {}, pageSize: {}, type: {}, status: {}",
                 current, pageSize, type, status);

        Page<Complaint> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();

        if (type != null && !type.trim().isEmpty()) {
            wrapper.eq(Complaint::getType, type.trim());
        }

        if (status != null) {
            wrapper.eq(Complaint::getStatus, status);
        }

        wrapper.orderByDesc(Complaint::getCreateTime);

        IPage<Complaint> pageResult = complaintRepository.selectPage(page, wrapper);

        PageVO<Complaint> pageVO = new PageVO<>();
        pageVO.setRecords(pageResult.getRecords());
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setCurrent(pageResult.getCurrent());
        pageVO.setPageSize(pageResult.getSize());
        pageVO.setPages(pageResult.getPages());

        return Result.success(pageVO);
    }

    /**
     * 获取投诉详情
     */
    @Operation(summary = "获取投诉详情", description = "根据ID获取投诉详细信息")
    @GetMapping("/detail/{id}")
    public Result<Complaint> getComplaintDetail(@PathVariable Long id) {
        log.info("获取投诉详情, id: {}", id);

        Complaint complaint = complaintRepository.selectById(id);
        if (complaint == null || complaint.getDeleted() == 1) {
            return Result.error("投诉不存在");
        }

        return Result.success(complaint);
    }

    /**
     * 处理投诉
     */
    @Operation(summary = "处理投诉", description = "回复和处理用户投诉")
    @PostMapping("/handle")
    public Result<Boolean> handleComplaint(@RequestBody Complaint request) {
        log.info("处理投诉, id: {}, handleResult: {}", request.getId(), request.getHandleResult());

        if (request.getId() == null) {
            return Result.error("投诉ID不能为空");
        }

        if (request.getHandleResult() == null || request.getHandleResult().trim().isEmpty()) {
            return Result.error("处理结果不能为空");
        }

        Complaint existing = complaintRepository.selectById(request.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("投诉不存在");
        }

        existing.setHandleResult(request.getHandleResult());
        existing.setHandleTime(LocalDateTime.now());
        existing.setStatus(1); // 1 = processed
        // Note: You might want to set handlerUserId from SecurityContext

        int rows = complaintRepository.updateById(existing);
        return rows > 0 ? Result.success(true) : Result.error("处理失败");
    }

    /**
     * 关闭投诉
     */
    @Operation(summary = "关闭投诉", description = "关闭投诉")
    @PostMapping("/close/{id}")
    public Result<Boolean> closeComplaint(@PathVariable Long id) {
        log.info("关闭投诉, id: {}", id);

        Complaint complaint = complaintRepository.selectById(id);
        if (complaint == null || complaint.getDeleted() == 1) {
            return Result.error("投诉不存在");
        }

        complaint.setStatus(2); // 2 = closed
        int rows = complaintRepository.updateById(complaint);

        return rows > 0 ? Result.success(true) : Result.error("操作失败");
    }

    /**
     * 删除投诉（逻辑删除）
     */
    @Operation(summary = "删除投诉", description = "逻辑删除投诉")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteComplaint(@PathVariable Long id) {
        log.info("删除投诉, id: {}", id);

        Complaint complaint = complaintRepository.selectById(id);
        if (complaint == null || complaint.getDeleted() == 1) {
            return Result.error("投诉不存在");
        }

        complaint.setDeleted(1);
        int rows = complaintRepository.updateById(complaint);
        return rows > 0 ? Result.success(true) : Result.error("删除失败");
    }
}
