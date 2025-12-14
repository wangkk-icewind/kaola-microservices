package com.kaola.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.product.model.entity.ProjectCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<ProjectCategory> {
}
