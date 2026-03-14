package com.kaola.masseur.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.masseur.model.entity.MasseurProject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 技师-项目关联数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface MasseurProjectMapper extends BaseMapper<MasseurProject> {

    /**
     * 根据技师ID查询关联的项目ID列表
     */
    @Select("SELECT project_id FROM masseur_project WHERE masseur_id = #{masseurId}")
    List<Long> findProjectIdsByMasseurId(@Param("masseurId") Long masseurId);

    /**
     * 根据项目ID查询关联的技师ID列表
     */
    @Select("SELECT masseur_id FROM masseur_project WHERE project_id = #{projectId}")
    List<Long> findMasseurIdsByProjectId(@Param("projectId") Long projectId);

    /**
     * 删除技师的所有项目关联
     */
    @Delete("DELETE FROM masseur_project WHERE masseur_id = #{masseurId}")
    int deleteByMasseurId(@Param("masseurId") Long masseurId);

    /**
     * 删除技师的指定项目关联
     */
    @Delete("DELETE FROM masseur_project WHERE masseur_id = #{masseurId} AND project_id = #{projectId}")
    int deleteByMasseurIdAndProjectId(@Param("masseurId") Long masseurId, @Param("projectId") Long projectId);

    /**
     * 检查技师和项目的关联是否存在
     */
    @Select("SELECT COUNT(*) FROM masseur_project WHERE masseur_id = #{masseurId} AND project_id = #{projectId}")
    int existsByMasseurIdAndProjectId(@Param("masseurId") Long masseurId, @Param("projectId") Long projectId);
}
