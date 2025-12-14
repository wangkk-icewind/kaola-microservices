package com.kaola.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.store.model.entity.Masseur;
import org.apache.ibatis.annotations.Mapper;

/**
 * 技师数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface MasseurMapper extends BaseMapper<Masseur> {
}
