package com.kaola.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.product.model.entity.MasseurLevelPricing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 技师等级价格系数配置数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface MasseurLevelPricingRepository extends BaseMapper<MasseurLevelPricing> {

    /**
     * 根据等级查询价格系数
     */
    @Select("SELECT * FROM t_masseur_level_pricing WHERE level = #{level} AND status = 1 AND deleted = 0")
    MasseurLevelPricing findByLevel(@Param("level") Integer level);

    /**
     * 根据等级获取价格系数值
     */
    @Select("SELECT multiplier FROM t_masseur_level_pricing WHERE level = #{level} AND status = 1 AND deleted = 0")
    BigDecimal getMultiplierByLevel(@Param("level") Integer level);

    /**
     * 查询所有启用的价格系数配置
     */
    @Select("SELECT * FROM t_masseur_level_pricing WHERE status = 1 AND deleted = 0 ORDER BY level")
    List<MasseurLevelPricing> findAllEnabled();

    /**
     * 查询所有价格系数配置（包括禁用的）
     */
    @Select("SELECT * FROM t_masseur_level_pricing WHERE deleted = 0 ORDER BY level")
    List<MasseurLevelPricing> findAll();
}
