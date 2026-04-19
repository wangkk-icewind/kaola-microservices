package com.kaola.store.service;

import com.kaola.store.model.entity.City;

import java.util.List;

public interface CityService {

    /** 获取城市列表（status=null 全部，status=1 仅上线） */
    List<City> getCities(Integer status);

    /** 搜索城市 */
    List<City> searchCities(String keyword);

    /** 创建城市 */
    City createCity(City city);

    /** 更新城市 */
    City updateCity(City city);

    /** 更新城市状态 */
    void updateCityStatus(Long id, Integer status);

    /** 删除城市 */
    void deleteCity(Long id);
}
