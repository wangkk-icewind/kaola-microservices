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
     * 根据门店ID和项目分类ID查询技师列表，按排序表权重排序，未配置的技师追加在末尾按评分降序
     */
    @Select("SELECT DISTINCT m.*, COALESCE(s.sort_order, 9999) AS effective_sort " +
            "FROM t_masseur m " +
            "INNER JOIN masseur_project mp ON m.id = mp.masseur_id " +
            "INNER JOIN t_project p ON mp.project_id = p.id " +
            "LEFT JOIN t_masseur_category_sort s ON s.masseur_id = m.id " +
            "  AND s.store_id = #{storeId} AND s.category_id = #{categoryId} " +
            "WHERE m.store_id = #{storeId} " +
            "AND p.category_id = #{categoryId} " +
            "AND m.status = 1 AND m.deleted = 0 " +
            "AND p.deleted = 0 " +
            "ORDER BY effective_sort ASC, m.rating DESC")
    List<Masseur> findByStoreIdAndCategoryId(@Param("storeId") Long storeId, @Param("categoryId") Long categoryId);

    /**
     * 获取门店名称
     */
    @Select("SELECT name FROM t_store WHERE id = #{storeId} AND deleted = 0 LIMIT 1")
    String findStoreNameById(@Param("storeId") Long storeId);

    /**
     * 获取门店详情（名称、电话、地址、营业时间、坐标）
     */
    @Select("SELECT name, phone, address, open_time, close_time, latitude, longitude " +
            "FROM t_store WHERE id = #{storeId} AND deleted = 0 LIMIT 1")
    java.util.Map<String, Object> findStoreInfoById(@Param("storeId") Long storeId);

    /**
     * 根据技师ID获取关联项目列表
     */
    @Select("SELECT p.id, p.name, p.description, p.base_price as price, p.duration, p.extra_price_per_minute as extraPricePerMinute " +
            "FROM masseur_project mp " +
            "JOIN t_project p ON mp.project_id = p.id " +
            "WHERE mp.masseur_id = #{masseurId} AND mp.deleted = 0 AND p.deleted = 0 AND p.status = 1 " +
            "ORDER BY p.id")
    List<java.util.Map<String, Object>> findProjectsByMasseurId(@Param("masseurId") Long masseurId);

    /**
     * 根据城市查询技师列表（JOIN门店表，兼容"芜湖"/"芜湖市"两种格式）
     */
    @Select("SELECT m.* FROM t_masseur m " +
            "INNER JOIN t_store s ON m.store_id = s.id " +
            "WHERE (s.city = #{city} OR CONCAT(s.city, '市') = #{city} OR s.city = REPLACE(#{city}, '市', '')) " +
            "AND m.status = 1 AND m.deleted = 0 " +
            "AND s.status = 1 AND s.deleted = 0 " +
            "ORDER BY m.rating DESC")
    List<Masseur> findByCity(@Param("city") String city);
}
