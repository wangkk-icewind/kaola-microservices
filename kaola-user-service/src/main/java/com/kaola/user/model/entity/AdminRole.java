package com.kaola.user.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员角色实体
 *
 * @author Kaola Team
 */
@Slf4j
@Data
@TableName("t_admin_role")
public class AdminRole implements Serializable {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     */
    @TableField("name")
    private String name;

    /**
     * 角色描述
     */
    @TableField("description")
    private String description;

    /**
     * 权限列表 (JSON数组字符串)
     */
    @TableField("permissions")
    @JsonIgnore
    private String permissions;

    /**
     * 状态 (0-禁用 1-正常)
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志 (0-未删除 1-已删除)
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    /**
     * 获取权限列表（将JSON字符串转换为数组）
     * 用于前端序列化
     */
    @JsonProperty("permissions")
    public List<String> getPermissionList() {
        if (permissions == null || permissions.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(permissions, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse permissions JSON: {}", permissions, e);
            return new ArrayList<>();
        }
    }

    /**
     * 设置权限列表（从数组转换为JSON字符串）
     * 用于前端反序列化
     */
    @JsonProperty("permissions")
    public void setPermissionList(List<String> permissionList) {
        if (permissionList == null || permissionList.isEmpty()) {
            this.permissions = "[]";
            return;
        }
        try {
            this.permissions = objectMapper.writeValueAsString(permissionList);
        } catch (JsonProcessingException e) {
            log.warn("Failed to convert permissions list to JSON: {}", permissionList, e);
            this.permissions = "[]";
        }
    }
}
