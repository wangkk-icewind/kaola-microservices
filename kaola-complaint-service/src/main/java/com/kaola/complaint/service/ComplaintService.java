package com.kaola.complaint.service;

import com.kaola.complaint.model.dto.ComplaintDTO;
import com.kaola.complaint.model.vo.ComplaintVO;

import java.util.List;

/**
 * 投诉服务接口
 *
 * @author Kaola Team
 */
public interface ComplaintService {

    /**
     * 创建投诉
     *
     * @param userId 用户ID
     * @param dto    投诉数据
     * @return 是否成功
     */
    boolean createComplaint(Long userId, ComplaintDTO dto);

    /**
     * 获取用户投诉列表
     *
     * @param userId 用户ID
     * @return 投诉列表
     */
    List<ComplaintVO> getComplaintList(Long userId);
}
