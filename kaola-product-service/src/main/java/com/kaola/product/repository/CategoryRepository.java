package com.kaola.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.product.model.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryRepository extends BaseMapper<Category> {

    @Select("SELECT * FROM t_category WHERE status = #{status} ORDER BY sort")
    List<Category> findByStatusOrderBySort(Integer status);
}
