package com.kaola.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaola.store.mapper.CityMapper;
import com.kaola.store.model.entity.City;
import com.kaola.store.service.CityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 城市服务实现
 *
 * @author Kaola Team
 */
@Slf4j
@Service
public class CityServiceImpl implements CityService {

    @Autowired
    private CityMapper cityMapper;

    @Cacheable(value = "cities", unless = "#result == null || #result.isEmpty()")
    @Override
    public List<City> getAllCities() {
        log.info("获取所有城市列表 - 从数据库查询");

        // 按拼音首字母排序
        LambdaQueryWrapper<City> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(City::getPinyinAbbr);

        return cityMapper.selectList(queryWrapper);
    }

    @Cacheable(value = "citiesSearch", key = "#keyword", unless = "#result == null || #result.isEmpty()")
    @Override
    public List<City> searchCities(String keyword) {
        log.info("搜索城市，关键词: {} - 从数据库查询", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCities();
        }

        // 支持按城市名或拼音搜索
        LambdaQueryWrapper<City> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .like(City::getCityName, keyword)
            .or()
            .like(City::getPinyin, keyword)
            .or()
            .like(City::getPinyinAbbr, keyword)
        );
        queryWrapper.orderByAsc(City::getPinyinAbbr);

        return cityMapper.selectList(queryWrapper);
    }
}
