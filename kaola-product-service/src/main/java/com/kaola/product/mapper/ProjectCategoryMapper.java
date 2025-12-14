package com.kaola.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.product.model.entity.ProjectCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 项目分类数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface ProjectCategoryMapper extends BaseMapper<ProjectCategory> {

    /**
     * 查询所有启用的分类
     */
    @Select("SELECT * FROM t_project_category WHERE status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<ProjectCategory> findAllActive();

    /**
     * 根据父级ID查询子分类
     */
    @Select("SELECT * FROM t_project_category WHERE parent_id = #{parentId} AND status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<ProjectCategory> findByParentId(@Param("parentId") Long parentId);

    /**
     * 查询顶级分类
     */
    @Select("SELECT * FROM t_project_category WHERE parent_id = 0 AND status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<ProjectCategory> findTopCategories();

    /**
     * 根据名称查询分类
     */
    @Select("SELECT * FROM t_project_category WHERE name = #{name} AND deleted = 0")
    ProjectCategory findByName(@Param("name") String name);
}
