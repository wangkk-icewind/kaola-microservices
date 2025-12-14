package com.kaola.review.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.PageVO;
import com.kaola.common.core.dto.Result;
import com.kaola.review.mapper.ReviewMapper;
import com.kaola.review.model.dto.ReviewDTO;
import com.kaola.review.model.entity.Review;
import com.kaola.review.model.vo.ReviewVO;
import com.kaola.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * 评价接口
 *
 * @author Kaola Team
 */
@Tag(name = "评价接口", description = "创建评价、查询评价等接口")
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    /**
     * 创建评价
     *
     * @param userId 用户ID
     * @param dto    评价数据
     * @return 操作结果
     */
    @Operation(summary = "创建评价", description = "用户对订单进行评价")
    @PostMapping("/create")
    public Result<Boolean> createReview(
            @Parameter(description = "用户ID", hidden = true)
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ReviewDTO dto) {
        boolean success = reviewService.createReview(userId, dto);
        return Result.success(success);
    }

    /**
     * 获取技师评价
     *
     * @param id   技师ID
     * @param page 页码
     * @param size 每页数量
     * @return 评价分页列表
     */
    @Operation(summary = "获取技师评价", description = "获取指定技师的评价列表")
    @GetMapping("/masseur/{id}")
    public Result<Page<ReviewVO>> getReviewsByMasseur(
            @Parameter(description = "技师ID", required = true)
            @PathVariable @NotNull(message = "技师ID不能为空") Long id,
            @Parameter(description = "页码")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量")
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ReviewVO> reviews = reviewService.getReviewsByMasseur(id, page, size);
        return Result.success(reviews);
    }

    /**
     * 获取门店评价
     *
     * @param id   门店ID
     * @param page 页码
     * @param size 每页数量
     * @return 评价分页列表
     */
    @Operation(summary = "获取门店评价", description = "获取指定门店的评价列表")
    @GetMapping("/store/{id}")
    public Result<Page<ReviewVO>> getReviewsByStore(
            @Parameter(description = "门店ID", required = true)
            @PathVariable @NotNull(message = "门店ID不能为空") Long id,
            @Parameter(description = "页码")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量")
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ReviewVO> reviews = reviewService.getReviewsByStore(id, page, size);
        return Result.success(reviews);
    }

    // ========== 管理后台接口 ==========

    /**
     * 分页查询评价列表（管理后台）
     */
    @Operation(summary = "分页查询评价列表", description = "支持技师ID、门店ID、状态筛选")
    @GetMapping("/list")
    public Result<PageVO<Review>> getReviewList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long masseurId,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Integer status) {

        Page<Review> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();

        if (masseurId != null) {
            wrapper.eq(Review::getMasseurId, masseurId);
        }

        if (storeId != null) {
            wrapper.eq(Review::getStoreId, storeId);
        }

        if (status != null) {
            wrapper.eq(Review::getStatus, status);
        }

        wrapper.orderByDesc(Review::getCreateTime);

        IPage<Review> pageResult = reviewMapper.selectPage(page, wrapper);

        PageVO<Review> pageVO = new PageVO<>();
        pageVO.setRecords(pageResult.getRecords());
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setCurrent(pageResult.getCurrent());
        pageVO.setPageSize(pageResult.getSize());
        pageVO.setPages(pageResult.getPages());

        return Result.success(pageVO);
    }

    /**
     * 获取评价详情（管理后台）
     */
    @Operation(summary = "获取评价详情", description = "根据ID获取评价详细信息")
    @GetMapping("/detail/{id}")
    public Result<Review> getReviewDetail(@PathVariable Long id) {
        Review review = reviewMapper.selectById(id);
        if (review == null || review.getDeleted() == 1) {
            return Result.error("评价不存在");
        }
        return Result.success(review);
    }

    /**
     * 回复评价（管理后台）
     */
    @Operation(summary = "回复评价", description = "商家回复用户评价")
    @PostMapping("/reply")
    public Result<Boolean> replyReview(@RequestBody Review request) {
        if (request.getId() == null) {
            return Result.error("评价ID不能为空");
        }

        if (request.getReply() == null || request.getReply().trim().isEmpty()) {
            return Result.error("回复内容不能为空");
        }

        Review existing = reviewMapper.selectById(request.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("评价不存在");
        }

        existing.setReply(request.getReply());
        int rows = reviewMapper.updateById(existing);
        return rows > 0 ? Result.success(true) : Result.error("回复失败");
    }

    /**
     * 删除评价（管理后台，逻辑删除）
     */
    @Operation(summary = "删除评价", description = "逻辑删除评价")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteReview(@PathVariable Long id) {
        Review review = reviewMapper.selectById(id);
        if (review == null || review.getDeleted() == 1) {
            return Result.error("评价不存在");
        }

        review.setDeleted(1);
        int rows = reviewMapper.updateById(review);
        return rows > 0 ? Result.success(true) : Result.error("删除失败");
    }
}
