package com.kaola.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.user.model.entity.AdminRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员角色 Mapper
 *
 * @author Kaola Team
 */
@Mapper
public interface AdminRoleMapper extends BaseMapper<AdminRole> {
}
