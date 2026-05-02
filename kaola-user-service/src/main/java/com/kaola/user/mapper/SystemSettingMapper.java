package com.kaola.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SystemSettingMapper {
    @Select("SELECT setting_value FROM t_system_setting WHERE setting_key = #{key}")
    String findValueByKey(@Param("key") String key);
}
