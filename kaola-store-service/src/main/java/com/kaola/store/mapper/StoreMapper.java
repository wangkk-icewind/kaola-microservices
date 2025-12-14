package com.kaola.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.store.model.entity.Store;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 门店数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface StoreMapper extends BaseMapper<Store> {

    /**
     * 查询所有营业中的门店
     */
    @Select("SELECT * FROM t_store WHERE status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<Store> findAllActive();

    /**
     * 根据城市查询门店
     */
    @Select("SELECT * FROM t_store WHERE city = #{city} AND status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<Store> findByCity(@Param("city") String city);

    /**
     * 分页查询门店
     */
    @Select("SELECT * FROM t_store WHERE status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    IPage<Store> findStorePage(Page<Store> page);

    /**
     * 根据名称模糊查询门店
     */
    @Select("SELECT * FROM t_store WHERE name LIKE CONCAT('%', #{name}, '%') AND status = 1 AND deleted = 0")
    List<Store> findByNameLike(@Param("name") String name);

    /**
     * 查询附近门店（根据经纬度范围）
     */
    @Select("SELECT *, " +
            "(6371 * acos(cos(radians(#{lat})) * cos(radians(latitude)) * cos(radians(longitude) - radians(#{lng})) + sin(radians(#{lat})) * sin(radians(latitude)))) AS distance " +
            "FROM t_store WHERE status = 1 AND deleted = 0 " +
            "HAVING distance < #{radius} ORDER BY distance ASC")
    List<Store> findNearbyStores(@Param("lat") Double lat, @Param("lng") Double lng, @Param("radius") Double radius);
}
