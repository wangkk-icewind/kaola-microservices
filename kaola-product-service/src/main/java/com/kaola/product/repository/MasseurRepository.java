package com.kaola.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.product.model.entity.Masseur;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 技师数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface MasseurRepository extends BaseMapper<Masseur> {

    /**
     * 根据门店ID查询技师列表
     */
    @Select("SELECT * FROM t_masseur WHERE store_id = #{storeId} AND status = 1 AND deleted = 0")
    List<Masseur> findByStoreId(@Param("storeId") Long storeId);

    /**
     * 根据用户ID查询技师
     */
    @Select("SELECT * FROM t_masseur WHERE user_id = #{userId} AND deleted = 0")
    Masseur findByUserId(@Param("userId") Long userId);

    /**
     * 分页查询可用技师
     */
    @Select("SELECT * FROM t_masseur WHERE status = 1 AND deleted = 0 ORDER BY rating DESC, order_count DESC")
    IPage<Masseur> findAvailableMasseurs(Page<Masseur> page);

    /**
     * 更新技师评分
     */
    @Update("UPDATE t_masseur SET rating = #{rating}, review_count = review_count + 1 WHERE id = #{masseurId}")
    int updateRating(@Param("masseurId") Long masseurId, @Param("rating") BigDecimal rating);

    /**
     * 增加订单数量
     */
    @Update("UPDATE t_masseur SET order_count = order_count + 1 WHERE id = #{masseurId}")
    int incrementOrderCount(@Param("masseurId") Long masseurId);

    /**
     * 更新技师余额
     */
    @Update("UPDATE t_masseur SET balance = balance + #{amount} WHERE id = #{masseurId}")
    int updateBalance(@Param("masseurId") Long masseurId, @Param("amount") BigDecimal amount);

    /**
     * 根据等级查询技师
     */
    @Select("SELECT * FROM t_masseur WHERE level = #{level} AND status = 1 AND deleted = 0")
    List<Masseur> findByLevel(@Param("level") Integer level);
}
