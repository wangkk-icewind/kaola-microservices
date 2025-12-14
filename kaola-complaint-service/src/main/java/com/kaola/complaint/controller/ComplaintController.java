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
@RequestMapping("/user-complaint")
@RequiredArgsConstructor
@Validated
public class ComplaintController {

    private final ComplaintService complaintService;

    /**
     * 创建投诉
     *
     * @param userId 用户ID
     * @param dto    投诉数据
     * @return 操作结果
     */
    @Operation(summary = "创建投诉", description = "用户创建投诉记录")
    @PostMapping("/create")
    public Result<Boolean> createComplaint(
            @Parameter(description = "用户ID", hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ComplaintDTO dto) {
        boolean success = complaintService.createComplaint(userId, dto);
        return Result.success(success);
    }

    /**
     * 获取投诉列表
     *
     * @param userId 用户ID
     * @return 投诉列表
     */
    @Operation(summary = "获取投诉列表", description = "获取用户的投诉记录列表")
    @GetMapping("/list")
    public Result<List<ComplaintVO>> getComplaintList(
            @Parameter(description = "用户ID", hidden = true)
            @RequestHeader("X-User-Id") Long userId) {
        List<ComplaintVO> complaints = complaintService.getComplaintList(userId);
        return Result.success(complaints);
    }
}
