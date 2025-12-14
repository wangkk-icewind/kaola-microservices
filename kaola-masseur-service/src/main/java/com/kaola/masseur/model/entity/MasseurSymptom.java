package com.kaola.masseur.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kaola.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 技师-症状关联实体
 *
 * @author Kaola Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_masseur_symptom")
public class MasseurSymptom extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 技师ID
     */
    @TableField("masseur_id")
    private Long masseurId;

    /**
     * 症状ID
     */
    @TableField("symptom_id")
    private Long symptomId;
}
