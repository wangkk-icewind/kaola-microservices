package com.kaola.masseur.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 技师-项目关联实体
 *
 * @author Kaola Team
 */
@Data
@TableName("masseur_project")
public class MasseurProject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 技师ID
     */
    @TableField("masseur_id")
    private Long masseurId;

    /**
     * 项目ID (关联product表)
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 创建时间
     */
    @TableField(value = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 删除标记 0:未删除 1:已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}
