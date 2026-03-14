package com.kaola.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.product.model.entity.TimeSlotPricing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 时段类型价格系数配置数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface TimeSlotPricingRepository extends BaseMapper<TimeSlotPricing> {

    /**
     * 根据时段类型查询价格系数
     */
    @Select("SELECT * FROM t_time_slot_pricing WHERE slot_type = #{slotType} AND status = 1 AND deleted = 0")
    List<TimeSlotPricing> findBySlotType(@Param("slotType") Integer slotType);

    /**
     * 查询所有启用的时段价格系数配置
     */
    @Select("SELECT * FROM t_time_slot_pricing WHERE status = 1 AND deleted = 0 ORDER BY slot_type, id")
    List<TimeSlotPricing> findAllEnabled();

    /**
     * 查询所有时段价格系数配置（包括禁用的）
     */
    @Select("SELECT * FROM t_time_slot_pricing WHERE deleted = 0 ORDER BY slot_type, id")
    List<TimeSlotPricing> findAll();
}
