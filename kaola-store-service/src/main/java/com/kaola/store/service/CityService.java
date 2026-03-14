package com.kaola.store.service;

import com.kaola.store.model.entity.City;

import java.util.List;

/**
 * 城市服务接口
 *
 * @author Kaola Team
 */
public interface CityService {

    /**
     * 获取所有城市列表
     *
     * @return 城市列表
     */
    List<City> getAllCities();

    /**
     * 搜索城市
     *
     * @param keyword 关键词（城市名或拼音）
     * @return 城市列表
     */
    List<City> searchCities(String keyword);
}
