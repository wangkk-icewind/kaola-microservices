package com.kaola.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.admin.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员用户Mapper
 *
 * @author Kaola Team
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
