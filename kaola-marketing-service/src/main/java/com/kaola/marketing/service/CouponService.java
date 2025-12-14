package com.kaola.marketing.service;

import com.kaola.common.core.dto.PageVO;
import com.kaola.marketing.model.entity.Coupon;

/**
 * 优惠券服务接口
 *
 * @author Kaola Team
 */
public interface CouponService {

    /**
     * 分页查询优惠券列表
     *
     * @param current  当前页
     * @param pageSize 每页大小
     * @param name     优惠券名称（模糊查询）
     * @param type     优惠券类型
     * @param status   状态
     * @return 分页结果
     */
    PageVO<Coupon> getCouponList(Long current, Long pageSize, String name, Integer type, Integer status);

    /**
     * 获取优惠券详情
     *
     * @param id 优惠券ID
     * @return 优惠券详情
     */
    Coupon getCouponDetail(Long id);

    /**
     * 创建优惠券
     *
     * @param coupon 优惠券信息
     * @return 是否成功
     */
    boolean createCoupon(Coupon coupon);

    /**
     * 更新优惠券
     *
     * @param coupon 优惠券信息
     * @return 是否成功
     */
    boolean updateCoupon(Coupon coupon);

    /**
     * 删除优惠券（软删除）
     *
     * @param id 优惠券ID
     * @return 是否成功
     */
    boolean deleteCoupon(Long id);

    /**
     * 更新优惠券状态
     *
     * @param id     优惠券ID
     * @param status 状态 (0-禁用 1-启用)
     * @return 是否成功
     */
    boolean updateCouponStatus(Long id, Integer status);
}
