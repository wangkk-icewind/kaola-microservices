package com.kaola.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    // TODO: 需要通过Feign调用其他服务
    // private final OrderFeignClient orderFeignClient;
    // private final UserFeignClient userFeignClient;

    @Override
    @Transactional
    public boolean createReview(Long userId, ReviewDTO dto) {
        log.info("创建评价, userId: {}, dto: {}", userId, dto);

        // TODO: 通过Feign调用订单服务验证订单
        // Order order = orderFeignClient.getOrderById(dto.getOrderId());
        // if (order == null) {
        //     throw new RuntimeException("订单不存在");
        // }

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

        // TODO: 通过Feign调用订单服务更新订单状态
        // orderFeignClient.updateOrderStatus(dto.getOrderId(), OrderStatus.REVIEWED);

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

        // TODO: 通过Feign调用用户服务填充用户信息
        // User user = userFeignClient.getUserById(review.getUserId());
        // if (user != null) {
        //     vo.setUserNickname(user.getNickname());
        //     vo.setUserAvatar(user.getAvatar());
        // }

        return vo;
    }
}
