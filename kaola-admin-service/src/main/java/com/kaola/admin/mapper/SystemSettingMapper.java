package com.kaola.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.admin.model.entity.SystemSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SystemSettingMapper extends BaseMapper<SystemSetting> {
    @Select("SELECT * FROM t_system_setting WHERE setting_group = #{group}")
    List<SystemSetting> findByGroup(@Param("group") String group);

    @Select("SELECT * FROM t_system_setting WHERE setting_key = #{key}")
    SystemSetting findByKey(@Param("key") String key);
}
