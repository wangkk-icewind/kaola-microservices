package com.kaola.masseur.service;

import com.kaola.masseur.model.dto.MasseurDTO;
import com.kaola.masseur.model.vo.MasseurVO;
// TODO: Cross-service dependency - LoginVO should be in common-core or fetched via auth service
// import com.kaola.common.core.vo.LoginVO;
// TODO: Cross-service dependency - TimeSlotVO should be in common-model or scheduling service
// import com.kaola.common.model.vo.TimeSlotVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 技师服务接口
 *
 * @author Kaola Team
 */
public interface MasseurService {

    // TODO: Cross-service dependency - Login should be handled by auth service
    // /**
    //  * 技师微信登录
    //  *
    //  * @param code 微信授权码
    //  * @return 登录结果
    //  */
    // LoginVO login(String code);

    /**
     * 获取技师信息
     *
     * @param masseurId 技师ID
     * @return 技师信息
     */
    MasseurVO getMasseurInfo(Long masseurId);

    /**
     * 更新技师信息
     *
     * @param masseurId 技师ID
     * @param dto       更新数据
     * @return 是否成功
     */
    boolean updateMasseurInfo(Long masseurId, MasseurDTO dto);

    /**
     * 获取门店技师列表
     *
     * @param storeId 门店ID
     * @return 技师列表
     */
    List<MasseurVO> getMasseursByStore(Long storeId);

    /**
     * 按症状获取技师
     *
     * @param symptomId 症状ID
     * @return 技师列表
     */
    List<MasseurVO> getMasseursBySymptom(Long symptomId);

    /**
     * 根据门店和症状获取技师
     *
     * @param storeId   门店ID
     * @param symptomId 症状ID (项目分类ID)
     * @return 技师列表
     */
    List<MasseurVO> getMasseursByStoreAndSymptom(Long storeId, Long symptomId);

    /**
     * 根据城市获取技师列表
     *
     * @param city 城市名称
     * @return 技师列表
     */
    List<MasseurVO> getMasseursByCity(String city);

    /**
     * 根据手机号查找技师
     *
     * @param phone 手机号
     * @return 技师信息，未找到返回 null
     */
    MasseurVO getMasseurByPhone(String phone);

    // TODO: Cross-service dependency - Scheduling should be handled by scheduling service via OpenFeign
    // /**
    //  * 获取技师可预约时间
    //  *
    //  * @param masseurId 技师ID
    //  * @param date      日期
    //  * @return 可预约时间段列表
    //  */
    // List<TimeSlotVO> getAvailableTime(Long masseurId, LocalDate date);
}
