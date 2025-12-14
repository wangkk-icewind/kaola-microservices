package com.kaola.masseur.service;

import com.kaola.common.model.vo.PageVO;
import com.kaola.masseur.model.entity.MasseurEarning;

/**
 * 技师收益服务接口
 *
 * @author Kaola Team
 */
public interface MasseurEarningService {

    /**
     * 分页查询技师收益记录
     *
     * @param masseurId 技师ID
     * @param current   当前页
     * @param pageSize  每页大小
     * @return 收益记录分页数据
     */
    PageVO<MasseurEarning> getEarningsByMasseur(Long masseurId, Long current, Long pageSize);

    /**
     * 分页查询所有收益记录
     *
     * @param masseurId 技师ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @param current   当前页
     * @param pageSize  每页大小
     * @return 收益记录分页数据
     */
    PageVO<MasseurEarning> getEarningsList(Long masseurId, String startDate, String endDate, Long current, Long pageSize);
}
