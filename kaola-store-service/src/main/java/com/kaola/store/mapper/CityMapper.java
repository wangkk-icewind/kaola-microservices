package com.kaola.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.store.model.entity.City;
import org.apache.ibatis.annotations.Mapper;

/**
 * 城市Mapper
 *
 * @author Kaola Team
 */
@Mapper
public interface CityMapper extends BaseMapper<City> {
}
