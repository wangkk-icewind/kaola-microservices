package com.kaola.marketing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.marketing.model.entity.Promotion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 促销活动数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface PromotionMapper extends BaseMapper<Promotion> {

    /**
     * 查询当前有效的促销活动
     */
    @Select("SELECT * FROM t_promotion WHERE status = 1 AND start_time <= #{now} AND end_time >= #{now} AND deleted = 0 ORDER BY sort_order ASC")
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);

    /**
     * 根据类型查询促销活动
     */
    @Select("SELECT * FROM t_promotion WHERE type = #{type} AND status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<Promotion> findByType(@Param("type") Integer type);

    /**
     * 查询首页Banner
     */
    @Select("SELECT * FROM t_promotion WHERE type = 1 AND status = 1 AND start_time <= #{now} AND end_time >= #{now} AND deleted = 0 ORDER BY sort_order ASC")
    List<Promotion> findBanners(@Param("now") LocalDateTime now);

    /**
     * 查询即将开始的活动
     */
    @Select("SELECT * FROM t_promotion WHERE status = 1 AND start_time > #{now} AND deleted = 0 ORDER BY start_time ASC")
    List<Promotion> findUpcomingPromotions(@Param("now") LocalDateTime now);
}
