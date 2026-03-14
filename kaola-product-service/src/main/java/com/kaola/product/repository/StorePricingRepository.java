package com.kaola.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.product.model.entity.StorePricing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 门店价格系数配置数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface StorePricingRepository extends BaseMapper<StorePricing> {

    /**
     * 根据门店ID查询价格系数
     */
    @Select("SELECT * FROM t_store_pricing WHERE store_id = #{storeId} AND status = 1 AND deleted = 0")
    StorePricing findByStoreId(@Param("storeId") Long storeId);

    /**
     * 根据门店ID获取价格系数值
     */
    @Select("SELECT multiplier FROM t_store_pricing WHERE store_id = #{storeId} AND status = 1 AND deleted = 0")
    BigDecimal getMultiplierByStoreId(@Param("storeId") Long storeId);

    /**
     * 查询所有启用的门店价格系数配置
     */
    @Select("SELECT * FROM t_store_pricing WHERE status = 1 AND deleted = 0 ORDER BY store_id")
    List<StorePricing> findAllEnabled();

    /**
     * 查询所有门店价格系数配置（包括禁用的）
     */
    @Select("SELECT * FROM t_store_pricing WHERE deleted = 0 ORDER BY store_id")
    List<StorePricing> findAll();

    /**
     * 批量查询门店价格系数
     */
    @Select("<script>" +
            "SELECT * FROM t_store_pricing " +
            "WHERE store_id IN " +
            "<foreach collection='storeIds' item='storeId' open='(' separator=',' close=')'>" +
            "#{storeId}" +
            "</foreach>" +
            " AND status = 1 AND deleted = 0" +
            "</script>")
    List<StorePricing> findByStoreIds(@Param("storeIds") List<Long> storeIds);
}
