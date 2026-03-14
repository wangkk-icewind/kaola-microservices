package com.kaola.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.product.model.entity.StorePricing;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门店价格系数配置Mapper
 *
 * @author Kaola Team
 */
@Mapper
public interface StorePricingMapper extends BaseMapper<StorePricing> {
}
