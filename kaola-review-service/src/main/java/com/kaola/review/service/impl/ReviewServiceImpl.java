package com.kaola.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.review.client.OrderServiceClient;
import com.kaola.review.client.UserServiceClient;
import com.kaola.review.model.dto.ReviewDTO;
import com.kaola.review.model.entity.Review;
import com.kaola.review.model.vo.ReviewVO;
import com.kaola.review.mapper.ReviewMapper;
import com.kaola.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * 评价服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderServiceClient orderServiceClient;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public boolean createReview(Long userId, ReviewDTO dto) {
        log.info("创建评价, userId: {}, orderId: {}", userId, dto.getOrderId());

        // 验证订单是否存在，并从订单补全 storeId / masseurId
        OrderServiceClient.OrderVO order;
        try {
            var orderResult = orderServiceClient.getOrderDetail(dto.getOrderId());
            if (orderResult == null || orderResult.getCode() != 0 || orderResult.getData() == null) {
                throw new RuntimeException("订单不存在");
            }
            order = orderResult.getData();
        } catch (feign.FeignException e) {
            log.error("调用 order-service 获取订单失败, orderId: {}", dto.getOrderId(), e);
            throw new RuntimeException("订单不存在或无法访问");
        }

        if (dto.getStoreId() == null)   dto.setStoreId(order.getStoreId());
        if (dto.getMasseurId() == null) dto.setMasseurId(order.getMasseurId());

        // 检查是否已评价
        Review existReview = reviewMapper.selectOne(
            new LambdaQueryWrapper<Review>()
                .eq(Review::getOrderId, dto.getOrderId())
        );
        if (existReview != null) {
            throw new RuntimeException("订单已评价");
        }

        // 创建评价
        Review review = new Review();
        review.setUserId(userId);
        review.setOrderId(dto.getOrderId());
        review.setStoreId(dto.getStoreId());
        review.setMasseurId(dto.getMasseurId());
        review.setRating(BigDecimal.valueOf(dto.getRating()));
        review.setContent(dto.getContent());
        review.setStatus(1); // 默认显示

        reviewMapper.insert(review);

        // 通知 order-service 将订单状态置为已评价
        try {
            orderServiceClient.reviewOrder(dto.getOrderId());
            log.info("订单状态已更新为已评价, orderId: {}", dto.getOrderId());
        } catch (Exception e) {
            log.warn("更新订单评价状态失败（非致命），orderId: {}", dto.getOrderId(), e);
        }

        return true;
    }

    @Override
    public Page<ReviewVO> getReviewsByMasseur(Long masseurId, Integer page, Integer size) {
        log.info("获取技师评价列表, masseurId: {}, page: {}, size: {}", masseurId, page, size);

        Page<Review> reviewPage = reviewMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<Review>()
                .eq(Review::getMasseurId, masseurId)
                .eq(Review::getStatus, 1)
                .orderByDesc(Review::getCreateTime)
        );

        Page<ReviewVO> voPage = new Page<>(page, size, reviewPage.getTotal());
        voPage.setRecords(
            reviewPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList())
        );
        return voPage;
    }

    @Override
    public Page<ReviewVO> getReviewsByStore(Long storeId, Integer page, Integer size) {
        log.info("获取门店评价列表, storeId: {}, page: {}, size: {}", storeId, page, size);

        Page<Review> reviewPage = reviewMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<Review>()
                .eq(Review::getStoreId, storeId)
                .eq(Review::getStatus, 1)
                .orderByDesc(Review::getCreateTime)
        );

        Page<ReviewVO> voPage = new Page<>(page, size, reviewPage.getTotal());
        voPage.setRecords(
            reviewPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList())
        );
        return voPage;
    }

    private ReviewVO convertToVO(Review review) {
        ReviewVO vo = new ReviewVO();
        BeanUtils.copyProperties(review, vo);

        // 通过 user-service 填充用户昵称和头像
        try {
            var userResult = userServiceClient.getUserInfo(review.getUserId());
            if (userResult != null && userResult.getCode() == 0 && userResult.getData() != null) {
                vo.setUserNickname(userResult.getData().getNickname());
                vo.setUserAvatar(userResult.getData().getAvatar());
            }
        } catch (Exception e) {
            log.warn("获取用户信息失败（非致命），userId: {}", review.getUserId(), e);
        }

        return vo;
    }
}
