package com.kaola.earning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.earning.model.entity.Withdrawal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 提现记录数据访问层
 *
 * @author Kaola Team
 */
@Mapper
public interface WithdrawalMapper extends BaseMapper<Withdrawal> {

    /**
     * 根据技师ID分页查询提现记录
     */
    @Select("SELECT * FROM t_withdrawal WHERE masseur_id = #{masseurId} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Withdrawal> findByMasseurId(Page<Withdrawal> page, @Param("masseurId") Long masseurId);

    /**
     * 根据提现单号查询
     */
    @Select("SELECT * FROM t_withdrawal WHERE withdrawal_no = #{withdrawalNo} AND deleted = 0")
    Withdrawal findByWithdrawalNo(@Param("withdrawalNo") String withdrawalNo);

    /**
     * 更新提现状态
     */
    @Update("UPDATE t_withdrawal SET status = #{status} WHERE id = #{withdrawalId}")
    int updateStatus(@Param("withdrawalId") Long withdrawalId, @Param("status") Integer status);

    /**
     * 处理提现
     */
    @Update("UPDATE t_withdrawal SET status = #{status}, remark = #{remark}, process_time = NOW() WHERE id = #{withdrawalId}")
    int processWithdrawal(@Param("withdrawalId") Long withdrawalId, @Param("status") Integer status, @Param("remark") String remark);

    /**
     * 统计技师提现总额
     */
    @Select("SELECT IFNULL(SUM(amount), 0) FROM t_withdrawal WHERE masseur_id = #{masseurId} AND status = 2 AND deleted = 0")
    BigDecimal sumSuccessByMasseurId(@Param("masseurId") Long masseurId);

    /**
     * 查询待处理提现
     */
    @Select("SELECT * FROM t_withdrawal WHERE status = 0 AND deleted = 0 ORDER BY create_time ASC")
    List<Withdrawal> findPendingWithdrawals();

    /**
     * 根据状态查询提现记录
     */
    @Select("SELECT * FROM t_withdrawal WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<Withdrawal> findByStatus(@Param("status") Integer status);
}
