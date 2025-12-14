package com.kaola.marketing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.PageVO;
import com.kaola.marketing.mapper.PromotionMapper;
import com.kaola.marketing.model.entity.Promotion;
import com.kaola.marketing.model.vo.CouponVO;
import com.kaola.marketing.model.vo.PromotionVO;
import com.kaola.marketing.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 促销服务实现类
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionMapper promotionMapper;

    @Override
    public List<PromotionVO> getActivePromotions(Long storeId) {
        log.info("获取活动促销, storeId: {}", storeId);
        // TODO: 实现促销活动查询逻辑
        return new ArrayList<>();
    }

    @Override
    public List<CouponVO> getAvailableCoupons(Long userId) {
        log.info("获取用户可用优惠券, userId: {}", userId);
        // TODO: 实现优惠券查询逻辑
        return new ArrayList<>();
    }

    @Override
    public boolean receiveCoupon(Long userId, Long couponId) {
        log.info("领取优惠券, userId: {}, couponId: {}", userId, couponId);
        // TODO: 实现优惠券领取逻辑
        return true;
    }

    @Override
    public boolean applyCoupon(Long orderId, Long couponId) {
        log.info("使用优惠券, orderId: {}, couponId: {}", orderId, couponId);
        // TODO: 实现优惠券使用逻辑
        return true;
    }

    // ==================== Admin 端方法实现 ====================

    @Override
    public PageVO<Promotion> getPromotionList(Long current, Long pageSize, String name, Integer type, Integer status) {
        log.info("分页查询促销活动列表, current: {}, pageSize: {}, name: {}, type: {}, status: {}",
                current, pageSize, name, type, status);

        Page<Promotion> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Promotion> wrapper = new LambdaQueryWrapper<>();

        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Promotion::getName, name.trim());
        }

        if (type != null) {
            wrapper.eq(Promotion::getType, type);
        }

        if (status != null) {
            wrapper.eq(Promotion::getStatus, status);
        }

        wrapper.orderByDesc(Promotion::getCreateTime);

        IPage<Promotion> pageResult = promotionMapper.selectPage(page, wrapper);

        // Convert IPage to PageVO
        PageVO<Promotion> pageVO = new PageVO<>();
        pageVO.setRecords(pageResult.getRecords());
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setCurrent(pageResult.getCurrent());
        pageVO.setPageSize(pageResult.getSize());
        pageVO.setPages(pageResult.getPages());

        return pageVO;
    }

    @Override
    public Promotion getPromotionDetail(Long id) {
        log.info("获取促销活动详情, id: {}", id);

        Promotion promotion = promotionMapper.selectById(id);
        if (promotion == null) {
            throw new RuntimeException("促销活动不存在");
        }

        return promotion;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPromotion(Promotion promotion) {
        log.info("创建促销活动, name: {}", promotion.getName());

        // 设置默认状态为启用
        if (promotion.getStatus() == null) {
            promotion.setStatus(1);
        }

        int rows = promotionMapper.insert(promotion);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePromotion(Promotion promotion) {
        log.info("更新促销活动, id: {}, name: {}", promotion.getId(), promotion.getName());

        if (promotion.getId() == null) {
            throw new RuntimeException("促销活动ID不能为空");
        }

        Promotion existingPromotion = promotionMapper.selectById(promotion.getId());
        if (existingPromotion == null) {
            throw new RuntimeException("促销活动不存在");
        }

        int rows = promotionMapper.updateById(promotion);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePromotion(Long id) {
        log.info("删除促销活动, id: {}", id);

        Promotion promotion = promotionMapper.selectById(id);
        if (promotion == null) {
            throw new RuntimeException("促销活动不存在");
        }

        // 软删除
        int rows = promotionMapper.deleteById(id);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePromotionStatus(Long id, Integer status) {
        log.info("更新促销活动状态, id: {}, status: {}", id, status);

        Promotion promotion = promotionMapper.selectById(id);
        if (promotion == null) {
            throw new RuntimeException("促销活动不存在");
        }

        promotion.setStatus(status);
        int rows = promotionMapper.updateById(promotion);
        return rows > 0;
    }
}
