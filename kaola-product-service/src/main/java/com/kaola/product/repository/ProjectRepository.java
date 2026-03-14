package com.kaola.product.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.product.model.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 项目数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface ProjectRepository extends BaseMapper<Project> {

    /**
     * 根据分类ID查询项目
     */
    @Select("SELECT * FROM t_project WHERE category_id = #{categoryId} AND status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<Project> findByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据门店ID查询项目
     */
    @Select("SELECT * FROM t_project WHERE store_id = #{storeId} AND status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<Project> findByStoreId(@Param("storeId") Long storeId);

    /**
     * 查询热门项目
     */
    @Select("SELECT * FROM t_project WHERE is_hot = 1 AND status = 1 AND deleted = 0 ORDER BY sales DESC LIMIT #{limit}")
    List<Project> findHotProjects(@Param("limit") Integer limit);

    /**
     * 查询推荐项目
     */
    @Select("SELECT * FROM t_project WHERE is_recommend = 1 AND status = 1 AND deleted = 0 ORDER BY sort_order ASC LIMIT #{limit}")
    List<Project> findRecommendProjects(@Param("limit") Integer limit);

    /**
     * 分页查询项目
     */
    @Select("SELECT * FROM t_project WHERE status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    IPage<Project> findProjectPage(Page<Project> page);

    /**
     * 根据名称模糊查询
     */
    @Select("SELECT * FROM t_project WHERE name LIKE CONCAT('%', #{name}, '%') AND status = 1 AND deleted = 0")
    List<Project> findByNameLike(@Param("name") String name);

    /**
     * 增加销量
     */
    @Select("UPDATE t_project SET sales = sales + 1 WHERE id = #{projectId}")
    int incrementSales(@Param("projectId") Long projectId);
}
