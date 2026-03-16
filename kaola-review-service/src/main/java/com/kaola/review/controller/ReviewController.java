package com.kaola.review.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.Result;
import com.kaola.review.client.MasseurServiceClient;
import com.kaola.review.client.OrderServiceClient;
import com.kaola.review.client.StoreServiceClient;
import com.kaola.review.client.UserServiceClient;
import com.kaola.review.dto.PageVO;
import com.kaola.review.mapper.ReviewMapper;
import com.kaola.review.model.dto.ReviewDTO;
import com.kaola.review.model.entity.Review;
import com.kaola.review.model.vo.ReviewVO;
import com.kaola.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * 评价接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "评价接口", description = "创建评价、查询评价等接口")
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;
    private final UserServiceClient userServiceClient;
    private final StoreServiceClient storeServiceClient;
    private final MasseurServiceClient masseurServiceClient;
    private final OrderServiceClient orderServiceClient;

    @Data
    public static class AdminReviewVO {
        private Long id;
        private Long userId;
        private String userName;
        private String userAvatar;
        private Long orderId;
        private String orderNo;
        private Long storeId;
        private String storeName;
        private Long masseurId;
        private String masseurName;
        private String projectName;
        private BigDecimal rating;
        private String content;
        private Object images;
        private String reply;
        private Integer status;
        private String createTime;
    }

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
     * 分页查询评价列表（管理后台，含用户/门店/技师/项目名称）
     */
    @Operation(summary = "分页查询评价列表", description = "支持技师ID、门店ID、状态筛选")
    @GetMapping("/list")
    public Result<PageVO<AdminReviewVO>> getReviewList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) Long masseurId,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Integer status) {

        Page<Review> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();

        if (masseurId != null) wrapper.eq(Review::getMasseurId, masseurId);
        if (storeId != null) wrapper.eq(Review::getStoreId, storeId);
        if (status != null) wrapper.eq(Review::getStatus, status);
        wrapper.orderByDesc(Review::getCreateTime);

        IPage<Review> pageResult = reviewMapper.selectPage(page, wrapper);
        List<Review> reviews = pageResult.getRecords();

        // 收集去重 ID
        Set<Long> userIds = new HashSet<>(), storeIds = new HashSet<>(),
                  masseurIds = new HashSet<>(), orderIds = new HashSet<>();
        for (Review r : reviews) {
            if (r.getUserId() != null) userIds.add(r.getUserId());
            if (r.getStoreId() != null) storeIds.add(r.getStoreId());
            if (r.getMasseurId() != null) masseurIds.add(r.getMasseurId());
            if (r.getOrderId() != null) orderIds.add(r.getOrderId());
        }

        // 批量查询名称
        Map<Long, UserServiceClient.UserVO> userMap = new HashMap<>();
        for (Long uid : userIds) {
            try {
                Result<UserServiceClient.UserVO> res = userServiceClient.getUserInfo(uid);
                if (res != null && res.getData() != null) userMap.put(uid, res.getData());
            } catch (Exception e) { log.warn("获取用户失败 userId={}", uid); }
        }
        Map<Long, String> storeMap = new HashMap<>();
        for (Long sid : storeIds) {
            try {
                Result<StoreServiceClient.StoreVO> res = storeServiceClient.getStoreDetail(sid);
                if (res != null && res.getData() != null) storeMap.put(sid, res.getData().getName());
            } catch (Exception e) { log.warn("获取门店失败 storeId={}", sid); }
        }
        Map<Long, String> masseurMap = new HashMap<>();
        for (Long mid : masseurIds) {
            try {
                Result<MasseurServiceClient.MasseurVO> res = masseurServiceClient.getMasseurDetail(mid);
                if (res != null && res.getData() != null) masseurMap.put(mid, res.getData().getName());
            } catch (Exception e) { log.warn("获取技师失败 masseurId={}", mid); }
        }
        Map<Long, OrderServiceClient.OrderVO> orderMap = new HashMap<>();
        for (Long oid : orderIds) {
            try {
                Result<OrderServiceClient.OrderVO> res = orderServiceClient.getOrderDetail(oid);
                if (res != null && res.getData() != null) orderMap.put(oid, res.getData());
            } catch (Exception e) { log.warn("获取订单失败 orderId={}", oid); }
        }

        // 组装 VO
        List<AdminReviewVO> voList = new ArrayList<>();
        for (Review r : reviews) {
            AdminReviewVO vo = new AdminReviewVO();
            vo.setId(r.getId());
            vo.setUserId(r.getUserId());
            vo.setOrderId(r.getOrderId());
            vo.setStoreId(r.getStoreId());
            vo.setMasseurId(r.getMasseurId());
            vo.setRating(r.getRating());
            vo.setContent(r.getContent());
            vo.setImages(r.getImages());
            vo.setReply(r.getReply());
            vo.setStatus(r.getStatus());
            vo.setCreateTime(r.getCreateTime() != null ? r.getCreateTime().toString() : null);

            UserServiceClient.UserVO uvo = userMap.get(r.getUserId());
            vo.setUserName(uvo != null && uvo.getNickname() != null ? uvo.getNickname() : "");
            vo.setUserAvatar(uvo != null ? uvo.getAvatar() : null);
            vo.setStoreName(storeMap.getOrDefault(r.getStoreId(), ""));
            vo.setMasseurName(masseurMap.getOrDefault(r.getMasseurId(), ""));
            OrderServiceClient.OrderVO ovo = orderMap.get(r.getOrderId());
            vo.setOrderNo(ovo != null ? ovo.getOrderNo() : "");
            vo.setProjectName(ovo != null ? ovo.getProjectName() : "");
            voList.add(vo);
        }

        PageVO<AdminReviewVO> pageVO = new PageVO<>();
        pageVO.setRecords(voList);
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
