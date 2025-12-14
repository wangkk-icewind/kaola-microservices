package com.kaola.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kaola.store.model.entity.Store;
import com.kaola.store.model.vo.StoreVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 门店服务接口
 *
 * @author Kaola Team
 */
public interface StoreService extends IService<Store> {

    /**
     * 获取附近门店
     *
     * @param lat  纬度
     * @param lng  经度
     * @param city 城市
     * @return 门店列表
     */
    List<StoreVO> getNearbyStores(BigDecimal lat, BigDecimal lng, String city);

    /**
     * 获取门店详情
     *
     * @param storeId 门店ID
     * @return 门店详情
     */
    StoreVO getStoreDetail(Long storeId);

    /**
     * 按城市获取门店
     *
     * @param city 城市名称
     * @return 门店列表
     */
    List<StoreVO> getStoresByCity(String city);
}
