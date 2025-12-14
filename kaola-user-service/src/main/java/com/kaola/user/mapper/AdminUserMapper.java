package com.kaola.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.user.model.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员用户 Mapper
 *
 * @author Kaola Team
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
