package com.kaola.marketing.service;

import com.kaola.common.core.dto.PageVO;
import com.kaola.marketing.model.entity.Promotion;
import com.kaola.marketing.model.vo.CouponVO;
import com.kaola.marketing.model.vo.PromotionVO;

import java.util.List;

/**
 * 促销服务接口
 *
 * @author Kaola Team
 */
public interface PromotionService {

    /**
     * 获取活动促销
     *
     * @param storeId 门店ID
     * @return 促销活动列表
     */
    List<PromotionVO> getActivePromotions(Long storeId);

    /**
     * 获取用户可用优惠券
     *
     * @param userId 用户ID
     * @return 优惠券列表
     */
    List<CouponVO> getAvailableCoupons(Long userId);

    /**
     * 领取优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 是否成功
     */
    boolean receiveCoupon(Long userId, Long couponId);

    /**
     * 使用优惠券
     *
     * @param orderId  订单ID
     * @param couponId 优惠券ID
     * @return 是否成功
     */
    boolean applyCoupon(Long orderId, Long couponId);

    // ==================== Admin 端方法 ====================

    /**
     * 分页查询促销活动列表
     *
     * @param current  当前页
     * @param pageSize 每页大小
     * @param name     活动名称（模糊查询）
     * @param type     活动类型
     * @param status   状态
     * @return 分页结果
     */
    PageVO<Promotion> getPromotionList(Long current, Long pageSize, String name, Integer type, Integer status);

    /**
     * 获取促销活动详情
     *
     * @param id 促销活动ID
     * @return 促销活动详情
     */
    Promotion getPromotionDetail(Long id);

    /**
     * 创建促销活动
     *
     * @param promotion 促销活动信息
     * @return 是否成功
     */
    boolean createPromotion(Promotion promotion);

    /**
     * 更新促销活动
     *
     * @param promotion 促销活动信息
     * @return 是否成功
     */
    boolean updatePromotion(Promotion promotion);

    /**
     * 删除促销活动（软删除）
     *
     * @param id 促销活动ID
     * @return 是否成功
     */
    boolean deletePromotion(Long id);

    /**
     * 更新促销活动状态
     *
     * @param id     促销活动ID
     * @param status 状态 (0-禁用 1-启用)
     * @return 是否成功
     */
    boolean updatePromotionStatus(Long id, Integer status);
}
