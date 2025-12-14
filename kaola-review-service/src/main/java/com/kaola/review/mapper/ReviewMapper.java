package com.kaola.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.review.model.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 评价数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    /**
     * 根据订单ID查询评价
     */
    @Select("SELECT * FROM t_review WHERE order_id = #{orderId} AND deleted = 0")
    Review findByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据技师ID分页查询评价
     */
    @Select("SELECT * FROM t_review WHERE masseur_id = #{masseurId} AND status = 1 AND deleted = 0 ORDER BY create_time DESC")
    IPage<Review> findByMasseurId(Page<Review> page, @Param("masseurId") Long masseurId);

    /**
     * 根据项目ID分页查询评价
     */
    @Select("SELECT * FROM t_review WHERE project_id = #{projectId} AND status = 1 AND deleted = 0 ORDER BY create_time DESC")
    IPage<Review> findByProjectId(Page<Review> page, @Param("projectId") Long projectId);

    /**
     * 根据用户ID查询评价
     */
    @Select("SELECT * FROM t_review WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<Review> findByUserId(@Param("userId") Long userId);

    /**
     * 计算技师平均评分
     */
    @Select("SELECT AVG(rating) FROM t_review WHERE masseur_id = #{masseurId} AND status = 1 AND deleted = 0")
    BigDecimal avgRatingByMasseurId(@Param("masseurId") Long masseurId);

    /**
     * 统计技师评价数量
     */
    @Select("SELECT COUNT(*) FROM t_review WHERE masseur_id = #{masseurId} AND status = 1 AND deleted = 0")
    int countByMasseurId(@Param("masseurId") Long masseurId);

    /**
     * 查询最新评价
     */
    @Select("SELECT * FROM t_review WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<Review> findLatestReviews(@Param("limit") Integer limit);
}
