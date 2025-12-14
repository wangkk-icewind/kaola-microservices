package com.kaola.review.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.review.model.dto.ReviewDTO;
import com.kaola.review.model.vo.ReviewVO;

/**
 * 评价服务接口
 *
 * @author Kaola Team
 */
public interface ReviewService {

    /**
     * 创建评价
     *
     * @param userId 用户ID
     * @param dto    评价数据
     * @return 是否成功
     */
    boolean createReview(Long userId, ReviewDTO dto);

    /**
     * 获取技师评价列表
     *
     * @param masseurId 技师ID
     * @param page      页码
     * @param size      每页数量
     * @return 评价分页列表
     */
    Page<ReviewVO> getReviewsByMasseur(Long masseurId, Integer page, Integer size);

    /**
     * 获取门店评价列表
     *
     * @param storeId 门店ID
     * @param page    页码
     * @param size    每页数量
     * @return 评价分页列表
     */
    Page<ReviewVO> getReviewsByStore(Long storeId, Integer page, Integer size);
}
