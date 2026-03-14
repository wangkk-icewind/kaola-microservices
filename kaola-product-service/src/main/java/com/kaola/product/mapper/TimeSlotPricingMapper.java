package com.kaola.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.product.model.entity.TimeSlotPricing;
import org.apache.ibatis.annotations.Mapper;

/**
 * 时段价格系数配置Mapper
 *
 * @author Kaola Team
 */
@Mapper
public interface TimeSlotPricingMapper extends BaseMapper<TimeSlotPricing> {
}
