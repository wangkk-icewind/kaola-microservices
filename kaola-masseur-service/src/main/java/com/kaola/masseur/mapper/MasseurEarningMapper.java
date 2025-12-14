package com.kaola.masseur.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.masseur.model.entity.MasseurEarning;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 技师收益数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface MasseurEarningMapper extends BaseMapper<MasseurEarning> {

    /**
     * 根据技师ID分页查询收益记录
     */
    @Select("SELECT * FROM t_masseur_earning WHERE masseur_id = #{masseurId} AND deleted = 0 ORDER BY create_time DESC")
    IPage<MasseurEarning> findByMasseurId(Page<MasseurEarning> page, @Param("masseurId") Long masseurId);

    /**
     * 根据订单ID查询收益记录
     */
    @Select("SELECT * FROM t_masseur_earning WHERE order_id = #{orderId} AND deleted = 0")
    MasseurEarning findByOrderId(@Param("orderId") Long orderId);

    /**
     * 统计技师总收益
     */
    @Select("SELECT IFNULL(SUM(amount), 0) FROM t_masseur_earning WHERE masseur_id = #{masseurId} AND deleted = 0")
    BigDecimal sumByMasseurId(@Param("masseurId") Long masseurId);

    /**
     * 统计技师指定日期范围内的收益
     */
    @Select("SELECT IFNULL(SUM(amount), 0) FROM t_masseur_earning " +
            "WHERE masseur_id = #{masseurId} AND DATE(create_time) BETWEEN #{startDate} AND #{endDate} AND deleted = 0")
    BigDecimal sumByMasseurIdAndDateRange(@Param("masseurId") Long masseurId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    /**
     * 按类型统计技师收益
     */
    @Select("SELECT IFNULL(SUM(amount), 0) FROM t_masseur_earning " +
            "WHERE masseur_id = #{masseurId} AND type = #{type} AND deleted = 0")
    BigDecimal sumByMasseurIdAndType(@Param("masseurId") Long masseurId, @Param("type") Integer type);

    /**
     * 查询技师最近的收益记录
     */
    @Select("SELECT * FROM t_masseur_earning WHERE masseur_id = #{masseurId} AND deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<MasseurEarning> findRecentByMasseurId(@Param("masseurId") Long masseurId, @Param("limit") Integer limit);
}
