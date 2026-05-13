package com.kaola.complaint.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.complaint.model.dto.ComplaintDTO;
import com.kaola.complaint.model.vo.ComplaintVO;
import com.kaola.complaint.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 投诉接口
 *
 * @author Kaola Team
 */
@Tag(name = "投诉接口", description = "创建投诉、查询投诉等接口")
@RestController
@RequestMapping("/complaint")
@RequiredArgsConstructor
@Validated
public class ComplaintController {

    private final ComplaintService complaintService;

    @Operation(summary = "提交投诉", description = "用户提交投诉记录")
    @PostMapping("/submit")
    public Result<Boolean> createComplaint(
            @Parameter(description = "用户ID", hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ComplaintDTO dto) {
        boolean success = complaintService.createComplaint(userId, dto);
        return Result.success(success);
    }

    @Operation(summary = "获取投诉列表", description = "获取用户的投诉记录列表")
    @GetMapping("/list")
    public Result<List<ComplaintVO>> getComplaintList(
            @Parameter(description = "用户ID", hidden = true)
            @RequestHeader("X-User-Id") Long userId) {
        List<ComplaintVO> complaints = complaintService.getComplaintList(userId);
        return Result.success(complaints);
    }

    @Operation(summary = "获取投诉详情", description = "根据投诉ID获取详细信息")
    @GetMapping("/detail/{id}")
    public Result<ComplaintVO> getComplaintDetail(
            @Parameter(description = "投诉ID", required = true)
            @PathVariable Long id) {
        ComplaintVO complaint = complaintService.getComplaintDetail(id);
        return Result.success(complaint);
    }
}
