package com.kaola.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.user.model.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户优惠券数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    /**
     * 根据用户ID查询优惠券
     */
    @Select("SELECT * FROM t_user_coupon WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<UserCoupon> findByUserId(@Param("userId") Long userId);

    /**
     * 查询用户可用优惠券
     */
    @Select("SELECT * FROM t_user_coupon WHERE user_id = #{userId} AND status = 1 AND expire_time > #{now} AND deleted = 0")
    List<UserCoupon> findAvailableByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * 查询用户指定优惠券领取次数
     */
    @Select("SELECT COUNT(*) FROM t_user_coupon WHERE user_id = #{userId} AND coupon_id = #{couponId} AND deleted = 0")
    int countByUserIdAndCouponId(@Param("userId") Long userId, @Param("couponId") Long couponId);

    /**
     * 使用优惠券
     */
    @Update("UPDATE t_user_coupon SET status = 2, use_time = #{useTime}, order_id = #{orderId} WHERE id = #{id} AND status = 1")
    int useCoupon(@Param("id") Long id, @Param("useTime") LocalDateTime useTime, @Param("orderId") Long orderId);

    /**
     * 查询用户可用于指定金额的优惠券
     */
    @Select("SELECT uc.* FROM t_user_coupon uc " +
            "INNER JOIN t_coupon c ON uc.coupon_id = c.id " +
            "WHERE uc.user_id = #{userId} AND uc.status = 1 AND uc.expire_time > #{now} " +
            "AND c.min_amount <= #{amount} AND uc.deleted = 0")
    List<UserCoupon> findUsableCoupons(@Param("userId") Long userId, @Param("amount") BigDecimal amount, @Param("now") LocalDateTime now);

    /**
     * 根据状态查询用户优惠券
     */
    @Select("SELECT * FROM t_user_coupon WHERE user_id = #{userId} AND status = #{status} AND deleted = 0")
    List<UserCoupon> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
}
