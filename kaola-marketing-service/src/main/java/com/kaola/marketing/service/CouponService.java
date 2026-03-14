package com.kaola.marketing.service;

import com.kaola.common.core.dto.PageVO;
import com.kaola.marketing.model.dto.CheckAvailableRequest;
import com.kaola.marketing.model.dto.ValidateCouponRequest;
import com.kaola.marketing.model.dto.ValidateCouponResponse;
import com.kaola.marketing.model.entity.Coupon;
import com.kaola.marketing.model.vo.AvailableCouponVO;
import com.kaola.marketing.model.vo.UserCouponVO;

import java.util.List;

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

    // ==================== 用户端接口 ====================

    /**
     * 获取用户优惠券列表
     *
     * @param userId 用户ID
     * @param status 状态 (1-未使用 2-已使用 3-已过期, null-全部)
     * @return 用户优惠券列表
     */
    List<UserCouponVO> getUserCouponList(Long userId, Integer status);

    /**
     * 领取优惠券
     *
     * @param couponId 优惠券ID
     * @param userId   用户ID
     * @return 是否成功
     */
    boolean claimCoupon(Long couponId, Long userId);

    /**
     * 获取订单可用的优惠券列表
     *
     * @param request 检查请求（包含订单金额、门店、项目等信息）
     * @return 可用优惠券列表（含优惠金额）
     */
    List<AvailableCouponVO> getAvailableCoupons(CheckAvailableRequest request);

    /**
     * 验证优惠券是否可用
     *
     * @param request 验证请求
     * @return 验证结果（是否可用、优惠金额、最终金额）
     */
    ValidateCouponResponse validateCoupon(ValidateCouponRequest request);
}
