package com.kaola.marketing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.marketing.model.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * 查询可领取的优惠券
     */
    @Select("SELECT * FROM t_coupon WHERE status = 1 AND start_time <= #{now} AND end_time >= #{now} " +
            "AND (total_count = 0 OR receive_count < total_count) AND deleted = 0 ORDER BY sort_order ASC")
    List<Coupon> findAvailableCoupons(@Param("now") LocalDateTime now);

    /**
     * 根据类型查询优惠券
     */
    @Select("SELECT * FROM t_coupon WHERE type = #{type} AND status = 1 AND deleted = 0")
    List<Coupon> findByType(@Param("type") Integer type);

    /**
     * 增加领取数量
     */
    @Update("UPDATE t_coupon SET receive_count = receive_count + 1 WHERE id = #{couponId} " +
            "AND (total_count = 0 OR receive_count < total_count)")
    int incrementReceiveCount(@Param("couponId") Long couponId);

    /**
     * 增加使用数量
     */
    @Update("UPDATE t_coupon SET use_count = use_count + 1 WHERE id = #{couponId}")
    int incrementUseCount(@Param("couponId") Long couponId);

    /**
     * 根据code查询优惠券
     */
    @Select("SELECT * FROM t_coupon WHERE code = #{code} AND deleted = 0")
    Coupon findByCode(@Param("code") String code);

    /**
     * 查询当前生效的「完成奖励券」（服务完成自动返券用），取一张。
     */
    @Select("SELECT * FROM t_coupon WHERE is_completion_reward = 1 AND status = 1 AND deleted = 0 " +
            "AND start_time <= NOW() AND end_time >= NOW() ORDER BY id LIMIT 1")
    Coupon findActiveCompletionReward();
}
