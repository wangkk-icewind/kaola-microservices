package com.kaola.marketing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.PageVO;
import com.kaola.marketing.mapper.CouponMapper;
import com.kaola.marketing.model.entity.Coupon;
import com.kaola.marketing.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
