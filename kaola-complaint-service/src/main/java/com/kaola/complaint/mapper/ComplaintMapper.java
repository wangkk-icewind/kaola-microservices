package com.kaola.complaint.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.complaint.model.entity.Complaint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 投诉数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {

    /**
     * 根据用户ID分页查询投诉
     */
    @Select("SELECT * FROM t_complaint WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Complaint> findByUserId(Page<Complaint> page, @Param("userId") Long userId);

    /**
     * 根据订单ID查询投诉
     */
    @Select("SELECT * FROM t_complaint WHERE order_id = #{orderId} AND deleted = 0")
    Complaint findByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据状态查询投诉
     */
    @Select("SELECT * FROM t_complaint WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<Complaint> findByStatus(@Param("status") Integer status);

    /**
     * 更新投诉状态
     */
    @Update("UPDATE t_complaint SET status = #{status} WHERE id = #{complaintId}")
    int updateStatus(@Param("complaintId") Long complaintId, @Param("status") Integer status);

    /**
     * 处理投诉
     */
    @Update("UPDATE t_complaint SET status = #{status}, reply = #{reply}, reply_time = NOW() WHERE id = #{complaintId}")
    int handleComplaint(@Param("complaintId") Long complaintId, @Param("status") Integer status, @Param("reply") String reply);

    /**
     * 统计待处理投诉数量
     */
    @Select("SELECT COUNT(*) FROM t_complaint WHERE status = 0 AND deleted = 0")
    int countPending();
}
