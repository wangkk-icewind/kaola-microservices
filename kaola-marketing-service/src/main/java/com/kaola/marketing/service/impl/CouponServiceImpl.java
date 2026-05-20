package com.kaola.marketing.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.PageVO;
import com.kaola.marketing.mapper.CouponMapper;
import com.kaola.marketing.mapper.UserCouponMapper;
import com.kaola.marketing.model.dto.CheckAvailableRequest;
import com.kaola.marketing.model.dto.CouponRules;
import com.kaola.marketing.model.dto.ValidateCouponRequest;
import com.kaola.marketing.model.dto.ValidateCouponResponse;
import com.kaola.marketing.model.entity.Coupon;
import com.kaola.marketing.model.entity.UserCoupon;
import com.kaola.marketing.model.vo.AvailableCouponVO;
import com.kaola.marketing.model.vo.UserCouponVO;
import com.kaola.marketing.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现类
 *
 * @author Kaola Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    @Override
    public PageVO<Coupon> getCouponList(Long current, Long pageSize, String name, Integer type, Integer status) {
        log.info("分页查询优惠券列表, current: {}, pageSize: {}, name: {}, type: {}, status: {}",
                current, pageSize, name, type, status);

        Page<Coupon> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();

        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Coupon::getName, name.trim());
        }

        if (type != null) {
            wrapper.eq(Coupon::getType, type);
        }

        if (status != null) {
            wrapper.eq(Coupon::getStatus, status);
        }

        wrapper.orderByDesc(Coupon::getCreateTime);

        IPage<Coupon> pageResult = couponMapper.selectPage(page, wrapper);

        // Convert IPage to PageVO
        PageVO<Coupon> pageVO = new PageVO<>();
        pageVO.setRecords(pageResult.getRecords());
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setCurrent(pageResult.getCurrent());
        pageVO.setPageSize(pageResult.getSize());
        pageVO.setPages(pageResult.getPages());

        return pageVO;
    }

    @Override
    public Coupon getCouponDetail(Long id) {
        log.info("获取优惠券详情, id: {}", id);

        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        return coupon;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCoupon(Coupon coupon) {
        log.info("创建优惠券, name: {}", coupon.getName());

        // 设置初始使用数量为0
        if (coupon.getUsedCount() == null) {
            coupon.setUsedCount(0);
        }

        // 设置默认状态为启用
        if (coupon.getStatus() == null) {
            coupon.setStatus(1);
        }

        int rows = couponMapper.insert(coupon);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCoupon(Coupon coupon) {
        log.info("更新优惠券, id: {}, name: {}", coupon.getId(), coupon.getName());

        if (coupon.getId() == null) {
            throw new RuntimeException("优惠券ID不能为空");
        }

        Coupon existingCoupon = couponMapper.selectById(coupon.getId());
        if (existingCoupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        int rows = couponMapper.updateById(coupon);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCoupon(Long id) {
        log.info("删除优惠券, id: {}", id);

        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        // 软删除
        int rows = couponMapper.deleteById(id);
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCouponStatus(Long id, Integer status) {
        log.info("更新优惠券状态, id: {}, status: {}", id, status);

        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        coupon.setStatus(status);
        int rows = couponMapper.updateById(coupon);
        return rows > 0;
    }

    // ==================== 用户端接口实现 ====================

    @Override
    public List<UserCouponVO> getUserCouponList(Long userId, Integer status) {
        log.info("获取用户优惠券列表, userId: {}, status: {}", userId, status);

        // 查询用户优惠券
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        if (status != null) {
            wrapper.eq(UserCoupon::getStatus, status);
        }
        wrapper.orderByDesc(UserCoupon::getCreateTime);

        List<UserCoupon> userCoupons = userCouponMapper.selectList(wrapper);

        // 转换为VO并填充优惠券详情
        return userCoupons.stream().map(userCoupon -> {
            UserCouponVO vo = new UserCouponVO();
            vo.setUserCouponId(userCoupon.getId());
            vo.setCouponId(userCoupon.getCouponId());
            vo.setStatus(userCoupon.getStatus());
            vo.setUseTime(userCoupon.getUseTime());
            vo.setOrderId(userCoupon.getOrderId());

            // 获取优惠券详情
            Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
            if (coupon != null) {
                vo.setName(coupon.getName());
                vo.setType(coupon.getType());
                vo.setValue(coupon.getValue());
                vo.setMinAmount(coupon.getMinAmount());
                vo.setStartTime(coupon.getStartTime());
                vo.setEndTime(coupon.getEndTime());
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized boolean claimCoupon(Long couponId, Long userId) {
        log.info("领取优惠券, couponId: {}, userId: {}", couponId, userId);

        // 1. 检查优惠券是否存在
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        // 2. 检查优惠券状态
        if (coupon.getStatus() != 1) {
            throw new RuntimeException("优惠券已禁用");
        }

        // 3. 检查库存
        if (coupon.getTotalCount() != null && coupon.getUsedCount() != null) {
            if (coupon.getUsedCount() >= coupon.getTotalCount()) {
                throw new RuntimeException("优惠券已领完");
            }
        }

        // 4. 检查时间
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartTime() != null && now.isBefore(coupon.getStartTime())) {
            throw new RuntimeException("优惠券未开始");
        }
        if (coupon.getEndTime() != null && now.isAfter(coupon.getEndTime())) {
            throw new RuntimeException("优惠券已过期");
        }

        // 5. 创建UserCoupon记录
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0); // 未使用 (0=未使用 1=已使用 2=已过期)

        int rows = userCouponMapper.insert(userCoupon);
        return rows > 0;
    }

    @Override
    public List<AvailableCouponVO> getAvailableCoupons(CheckAvailableRequest request) {
        log.info("获取可用优惠券列表, userId: {}, orderAmount: {}", request.getUserId(), request.getOrderAmount());

        // 1. 查询用户所有未使用的优惠券
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, request.getUserId())
                .eq(UserCoupon::getStatus, 0); // 未使用 (0=未使用 1=已使用 2=已过期)

        List<UserCoupon> userCoupons = userCouponMapper.selectList(wrapper);

        // 2. 过滤并计算优惠金额
        LocalDateTime now = LocalDateTime.now();
        List<AvailableCouponVO> result = new ArrayList<>();

        for (UserCoupon userCoupon : userCoupons) {
            Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
            if (coupon == null || coupon.getStatus() != 1) {
                continue;
            }

            AvailableCouponVO vo = new AvailableCouponVO();
            vo.setUserCouponId(userCoupon.getId());
            vo.setCouponId(coupon.getId());
            vo.setName(coupon.getName());
            vo.setType(coupon.getType());
            vo.setValue(coupon.getValue());
            vo.setMinAmount(coupon.getMinAmount());

            // 检查是否可用
            String reason = checkCouponAvailability(coupon, request, now);
            if (reason == null) {
                // 可用，计算优惠金额
                BigDecimal discountAmount = calculateDiscount(coupon, request.getOrderAmount());
                vo.setDiscountAmount(discountAmount);
                vo.setCanUse(true);
                vo.setReason("");
            } else {
                // 不可用
                vo.setDiscountAmount(BigDecimal.ZERO);
                vo.setCanUse(false);
                vo.setReason(reason);
            }

            result.add(vo);
        }

        // 3. 按优惠金额降序排序
        result.sort(Comparator.comparing(AvailableCouponVO::getDiscountAmount).reversed());

        return result;
    }

    @Override
    public ValidateCouponResponse validateCoupon(ValidateCouponRequest request) {
        log.info("验证优惠券, userCouponId: {}, userId: {}", request.getUserCouponId(), request.getUserId());

        ValidateCouponResponse response = new ValidateCouponResponse();

        // 1. 检查用户优惠券
        UserCoupon userCoupon = userCouponMapper.selectById(request.getUserCouponId());
        if (userCoupon == null) {
            response.setValid(false);
            response.setReason("优惠券不存在");
            return response;
        }

        if (!userCoupon.getUserId().equals(request.getUserId())) {
            response.setValid(false);
            response.setReason("优惠券不属于当前用户");
            return response;
        }

        if (userCoupon.getStatus() != 0) {
            response.setValid(false);
            response.setReason("优惠券已使用或已过期");
            return response;
        }

        // 2. 检查优惠券
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon == null || coupon.getStatus() != 1) {
            response.setValid(false);
            response.setReason("优惠券不可用");
            return response;
        }

        // 3. 检查可用性
        LocalDateTime now = LocalDateTime.now();
        String reason = checkCouponAvailability(coupon, request, now);
        if (reason != null) {
            response.setValid(false);
            response.setReason(reason);
            return response;
        }

        // 4. 计算优惠金额
        BigDecimal discountAmount = calculateDiscount(coupon, request.getOrderAmount());
        BigDecimal finalAmount = request.getOrderAmount().subtract(discountAmount);

        response.setValid(true);
        response.setDiscountAmount(discountAmount);
        response.setFinalAmount(finalAmount);
        response.setReason("");

        return response;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 检查优惠券可用性
     */
    private String checkCouponAvailability(Coupon coupon, Object request, LocalDateTime now) {
        // 检查时间
        if (coupon.getStartTime() != null && now.isBefore(coupon.getStartTime())) {
            return "优惠券未开始";
        }
        if (coupon.getEndTime() != null && now.isAfter(coupon.getEndTime())) {
            return "优惠券已过期";
        }

        // 获取订单金额和门店/项目信息
        BigDecimal orderAmount = null;
        Long storeId = null;
        List<Long> projectIds = null;
        Boolean isNewCustomer = null;

        if (request instanceof CheckAvailableRequest) {
            CheckAvailableRequest req = (CheckAvailableRequest) request;
            orderAmount = req.getOrderAmount();
            storeId = req.getStoreId();
            projectIds = req.getProjectIds();
            isNewCustomer = req.getIsNewCustomer();
        } else if (request instanceof ValidateCouponRequest) {
            ValidateCouponRequest req = (ValidateCouponRequest) request;
            orderAmount = req.getOrderAmount();
            storeId = req.getStoreId();
            projectIds = req.getProjectIds();
            isNewCustomer = req.getIsNewCustomer();
        }

        // 检查客群限制 (customer_type: 0=全部 1=新客 2=老客)
        if (coupon.getCustomerType() != null && coupon.getCustomerType() != 0) {
            if (coupon.getCustomerType() == 1) {
                // 新客专属券：isNewCustomer 必须为 true
                if (!Boolean.TRUE.equals(isNewCustomer)) {
                    return "该优惠券仅限新客使用";
                }
            } else if (coupon.getCustomerType() == 2) {
                // 老客专属券：isNewCustomer 必须为 false（非新客）
                if (Boolean.TRUE.equals(isNewCustomer)) {
                    return "该优惠券仅限老客使用";
                }
            }
        }

        // 检查最低消费金额（代金券除外）
        if (coupon.getType() != 3 && coupon.getMinAmount() != null && orderAmount != null) {
            if (orderAmount.compareTo(coupon.getMinAmount()) < 0) {
                return "订单金额不满足最低消费要求";
            }
        }

        // 检查规则限制
        if (coupon.getRules() != null && !coupon.getRules().trim().isEmpty()) {
            CouponRules rules = parseRules(coupon.getRules());
            if (!checkRules(rules, storeId, projectIds)) {
                return "不满足优惠券使用规则";
            }
        }

        return null;
    }

    /**
     * 计算优惠金额
     */
    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount) {
        switch (coupon.getType()) {
            case 1: // 满减券
                if (coupon.getMinAmount() != null && orderAmount.compareTo(coupon.getMinAmount()) >= 0) {
                    return coupon.getValue();
                }
                return BigDecimal.ZERO;

            case 2: // 折扣券
                if (coupon.getMinAmount() != null && orderAmount.compareTo(coupon.getMinAmount()) >= 0) {
                    // value是折扣率，如0.8表示8折，优惠金额 = 订单金额 * (1 - 折扣率)
                    BigDecimal discount = orderAmount.multiply(BigDecimal.ONE.subtract(coupon.getValue()))
                            .setScale(0, java.math.RoundingMode.FLOOR);
                    CouponRules rules = parseRules(coupon.getRules());
                    if (rules.getMaxDiscount() != null && rules.getMaxDiscount().compareTo(BigDecimal.ZERO) > 0) {
                        discount = discount.min(rules.getMaxDiscount());
                    }
                    return discount;
                }
                return BigDecimal.ZERO;

            case 3: // 代金券
                return coupon.getValue().min(orderAmount);

            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * 解析优惠券规则
     */
    private CouponRules parseRules(String rulesJson) {
        try {
            return JSON.parseObject(rulesJson, CouponRules.class);
        } catch (Exception e) {
            log.error("解析优惠券规则失败: {}", rulesJson, e);
            return new CouponRules();
        }
    }

    /**
     * 检查规则限制
     */
    private boolean checkRules(CouponRules rules, Long storeId, List<Long> projectIds) {
        // 检查门店限制
        if (rules.getStoreIds() != null && !rules.getStoreIds().isEmpty()) {
            if (storeId == null || !rules.getStoreIds().contains(storeId)) {
                return false;
            }
        }

        // 检查排除门店
        if (rules.getExcludeStoreIds() != null && storeId != null) {
            if (rules.getExcludeStoreIds().contains(storeId)) {
                return false;
            }
        }

        // 检查项目限制
        if (rules.getProjectIds() != null && !rules.getProjectIds().isEmpty()) {
            if (projectIds == null || projectIds.isEmpty()) {
                return false;
            }
            // 至少有一个项目在允许列表中
            boolean hasMatch = projectIds.stream().anyMatch(rules.getProjectIds()::contains);
            if (!hasMatch) {
                return false;
            }
        }

        // 检查排除项目
        if (rules.getExcludeProjectIds() != null && projectIds != null) {
            // 不能包含任何排除的项目
            boolean hasExcluded = projectIds.stream().anyMatch(rules.getExcludeProjectIds()::contains);
            if (hasExcluded) {
                return false;
            }
        }

        return true;
    }
}
