package com.kaola.marketing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.PageVO;
import com.kaola.marketing.mapper.CouponMapper;
import com.kaola.marketing.mapper.PromotionMapper;
import com.kaola.marketing.mapper.UserCouponMapper;
import com.kaola.marketing.model.entity.Coupon;
import com.kaola.marketing.model.entity.Promotion;
import com.kaola.marketing.model.entity.UserCoupon;
import com.kaola.marketing.model.vo.CouponVO;
import com.kaola.marketing.model.vo.PromotionVO;
import com.kaola.marketing.service.CouponService;
import com.kaola.marketing.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    private final CouponService couponService;

    @Override
    public List<PromotionVO> getActivePromotions(Long storeId) {
        log.info("获取活动促销, storeId: {}", storeId);

        // 构建查询条件
        LambdaQueryWrapper<Promotion> wrapper = new LambdaQueryWrapper<>();

        // 状态为启用
        wrapper.eq(Promotion::getStatus, 1);

        // 当前时间在活动有效期内
        wrapper.le(Promotion::getStartTime, LocalDateTime.now());
        wrapper.ge(Promotion::getEndTime, LocalDateTime.now());

        // 只返回 Banner 类别（首页轮播）
        wrapper.eq(Promotion::getCategory, "banner");

        // 按创建时间倒序
        wrapper.orderByDesc(Promotion::getCreateTime);

        // 查询数据库
        List<Promotion> promotions = promotionMapper.selectList(wrapper);

        // 转换为VO
        List<PromotionVO> promotionVOList = new ArrayList<>();
        for (Promotion promotion : promotions) {
            PromotionVO vo = new PromotionVO();
            vo.setId(promotion.getId());
            vo.setName(promotion.getName());
            vo.setImage(promotion.getImage());
            vo.setDescription(promotion.getDescription());
            vo.setLinkUrl(promotion.getLinkUrl());
            vo.setType(promotion.getType());
            vo.setStartTime(promotion.getStartTime());
            vo.setEndTime(promotion.getEndTime());
            vo.setRules(promotion.getRules());
            vo.setIsActive(true); // 查询出来的都是活动中的

            // 计算剩余时间
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endTime = promotion.getEndTime();
            long days = java.time.Duration.between(now, endTime).toDays();
            if (days > 0) {
                vo.setRemainingTime("剩余" + days + "天");
            } else {
                long hours = java.time.Duration.between(now, endTime).toHours();
                if (hours > 0) {
                    vo.setRemainingTime("剩余" + hours + "小时");
                } else {
                    vo.setRemainingTime("即将结束");
                }
            }

            promotionVOList.add(vo);
        }

        log.info("查询到 {} 个活动促销", promotionVOList.size());
        return promotionVOList;
    }

    @Override
    public List<CouponVO> getAvailableCoupons(Long userId) {
        log.info("获取用户可用优惠券, userId: {}", userId);
        LocalDateTime now = LocalDateTime.now();
        List<UserCoupon> userCoupons = userCouponMapper.selectList(
            new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getStatus, 0)
        );
        List<CouponVO> result = new ArrayList<>();
        for (UserCoupon uc : userCoupons) {
            Coupon coupon = couponMapper.selectById(uc.getCouponId());
            if (coupon == null || coupon.getStatus() != 1) continue;
            if (coupon.getEndTime() != null && now.isAfter(coupon.getEndTime())) continue;
            CouponVO vo = new CouponVO();
            vo.setId(coupon.getId());
            vo.setName(coupon.getName());
            vo.setType(coupon.getType());
            vo.setValue(coupon.getValue());
            vo.setMinAmount(coupon.getMinAmount());
            vo.setStartTime(coupon.getStartTime());
            vo.setEndTime(coupon.getEndTime());
            result.add(vo);
        }
        log.info("用户 {} 有 {} 张可用优惠券", userId, result.size());
        return result;
    }

    @Override
    public boolean receiveCoupon(Long userId, Long couponId) {
        log.info("领取优惠券, userId: {}, couponId: {}", userId, couponId);
        return couponService.claimCoupon(couponId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyCoupon(Long orderId, Long couponId) {
        log.info("使用优惠券, orderId: {}, userCouponId: {}", orderId, couponId);
        UserCoupon userCoupon = userCouponMapper.selectById(couponId);
        if (userCoupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        if (userCoupon.getStatus() != 0) {
            throw new RuntimeException("优惠券已使用或已过期");
        }
        userCoupon.setStatus(1);
        userCoupon.setOrderId(orderId);
        userCoupon.setUseTime(LocalDateTime.now());
        return userCouponMapper.updateById(userCoupon) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean restoreCoupon(Long userCouponId) {
        log.info("回退优惠券, userCouponId: {}", userCouponId);
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null) {
            log.warn("回退优惠券：券不存在 userCouponId={}", userCouponId);
            return false;
        }
        // 仅已使用(1)的券需要回退；其它状态忽略，保持幂等
        if (userCoupon.getStatus() == null || userCoupon.getStatus() != 1) {
            return true;
        }
        userCoupon.setStatus(0);
        userCoupon.setOrderId(null);
        userCoupon.setUseTime(null);
        return userCouponMapper.updateById(userCoupon) > 0;
    }

    @Override
    public List<PromotionVO> getStoreDiscounts(Long storeId) {
        log.info("获取门店时段折扣, storeId: {}", storeId);

        LambdaQueryWrapper<Promotion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Promotion::getStatus, 1);
        wrapper.eq(Promotion::getCategory, "store_discount");
        wrapper.eq(Promotion::getStoreId, storeId);
        wrapper.le(Promotion::getStartTime, LocalDateTime.now());
        wrapper.ge(Promotion::getEndTime, LocalDateTime.now());
        wrapper.orderByAsc(Promotion::getCreateTime);

        List<Promotion> promotions = promotionMapper.selectList(wrapper);

        List<PromotionVO> result = new ArrayList<>();
        for (Promotion p : promotions) {
            PromotionVO vo = new PromotionVO();
            vo.setId(p.getId());
            vo.setName(p.getName());
            vo.setDescription(p.getDescription());
            vo.setType(p.getType());
            vo.setRules(p.getRules());
            vo.setStartTime(p.getStartTime());
            vo.setEndTime(p.getEndTime());
            vo.setStoreId(p.getStoreId());
            vo.setIsActive(true);
            result.add(vo);
        }

        log.info("门店{}共有{}条时段折扣", storeId, result.size());
        return result;
    }

    // ==================== Admin 端方法实现 ====================

    @Override
    public PageVO<Promotion> getPromotionList(Long current, Long pageSize, String name, Integer type, Integer status, String category) {
        log.info("分页查询促销活动列表, current: {}, pageSize: {}, name: {}, type: {}, status: {}, category: {}",
                current, pageSize, name, type, status, category);

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

        // category 过滤：banner=首页促销, store_discount=门店时段折扣
        if (category != null && !category.trim().isEmpty()) {
            wrapper.eq(Promotion::getCategory, category.trim());
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

        // 使用 LambdaUpdateWrapper 直接软删除，兼容 deleted 字段为 null 的历史数据
        LambdaUpdateWrapper<Promotion> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Promotion::getId, id);
        wrapper.set(Promotion::getDeleted, 1);
        int rows = promotionMapper.update(null, wrapper);
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
