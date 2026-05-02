package com.kaola.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_system_setting")
public class SystemSetting {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("setting_key")
    private String settingKey;
    @TableField("setting_value")
    private String settingValue;
    @TableField("setting_group")
    private String settingGroup;
    private String description;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
}
