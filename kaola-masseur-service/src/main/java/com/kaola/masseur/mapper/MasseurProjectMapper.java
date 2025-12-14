package com.kaola.masseur.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.masseur.model.entity.MasseurProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 技师-服务项目关联数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface MasseurProjectMapper extends BaseMapper<MasseurProject> {

    @Select("SELECT * FROM t_masseur_project WHERE masseur_id = #{masseurId}")
    List<MasseurProject> findByMasseurId(Long masseurId);

    @Select("SELECT * FROM t_masseur_project WHERE project_id = #{projectId}")
    List<MasseurProject> findByProjectId(Long projectId);
}
