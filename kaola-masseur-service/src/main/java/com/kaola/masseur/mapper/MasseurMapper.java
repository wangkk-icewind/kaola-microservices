package com.kaola.masseur.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.masseur.model.entity.Masseur;
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
public interface MasseurMapper extends BaseMapper<Masseur> {

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

    /**
     * 根据项目分类ID查询技师列表（通过masseur_project和t_project表关联）
     */
    @Select("SELECT DISTINCT m.* FROM t_masseur m " +
            "INNER JOIN masseur_project mp ON m.id = mp.masseur_id " +
            "INNER JOIN t_project p ON mp.project_id = p.id " +
            "WHERE p.category_id = #{categoryId} " +
            "AND m.status = 1 AND m.deleted = 0 " +
            "AND p.deleted = 0 " +
            "ORDER BY m.rating DESC")
    List<Masseur> findByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据门店ID和项目分类ID查询技师列表
     */
    @Select("SELECT DISTINCT m.* FROM t_masseur m " +
            "INNER JOIN masseur_project mp ON m.id = mp.masseur_id " +
            "INNER JOIN t_project p ON mp.project_id = p.id " +
            "WHERE m.store_id = #{storeId} " +
            "AND p.category_id = #{categoryId} " +
            "AND m.status = 1 AND m.deleted = 0 " +
            "AND p.deleted = 0 " +
            "ORDER BY m.rating DESC")
    List<Masseur> findByStoreIdAndCategoryId(@Param("storeId") Long storeId, @Param("categoryId") Long categoryId);
}
