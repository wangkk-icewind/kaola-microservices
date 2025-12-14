package com.kaola.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaola.user.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据openId查询用户
     */
    @Select("SELECT * FROM t_user WHERE open_id = #{openId} AND deleted = 0")
    User findByOpenId(@Param("openId") String openId);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM t_user WHERE phone = #{phone} AND deleted = 0")
    User findByPhone(@Param("phone") String phone);

    /**
     * 根据unionId查询用户
     */
    @Select("SELECT * FROM t_user WHERE union_id = #{unionId} AND deleted = 0")
    User findByUnionId(@Param("unionId") String unionId);

    /**
     * 更新用户积分
     */
    @Update("UPDATE t_user SET point = point + #{point} WHERE id = #{userId}")
    int updatePoint(@Param("userId") Long userId, @Param("point") Integer point);

    /**
     * 更新用户等级
     */
    @Update("UPDATE t_user SET level = #{level} WHERE id = #{userId}")
    int updateLevel(@Param("userId") Long userId, @Param("level") Integer level);
}
