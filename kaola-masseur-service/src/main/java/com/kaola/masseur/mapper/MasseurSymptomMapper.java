package com.kaola.masseur.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.masseur.model.entity.MasseurSymptom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 技师-症状关联数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface MasseurSymptomMapper extends BaseMapper<MasseurSymptom> {

    @Select("SELECT * FROM t_masseur_symptom WHERE masseur_id = #{masseurId}")
    List<MasseurSymptom> findByMasseurId(Long masseurId);

    @Select("SELECT * FROM t_masseur_symptom WHERE symptom_id = #{symptomId}")
    List<MasseurSymptom> findBySymptomId(Long symptomId);
}
