package com.kaola.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kaola.store.mapper.CityMapper;
import com.kaola.store.model.entity.City;
import com.kaola.store.service.CityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CityServiceImpl implements CityService {

    @Autowired
    private CityMapper cityMapper;

    @Override
    public List<City> getCities(Integer status) {
        LambdaQueryWrapper<City> qw = new LambdaQueryWrapper<>();
        if (status != null) {
            qw.eq(City::getStatus, status);
        }
        qw.orderByAsc(City::getSortOrder).orderByAsc(City::getPinyinAbbr);
        return cityMapper.selectList(qw);
    }

    @Override
    public List<City> searchCities(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getCities(1);
        }
        LambdaQueryWrapper<City> qw = new LambdaQueryWrapper<>();
        qw.eq(City::getStatus, 1);
        qw.and(w -> w.like(City::getCityName, keyword)
                .or().like(City::getPinyin, keyword)
                .or().like(City::getPinyinAbbr, keyword));
        qw.orderByAsc(City::getSortOrder).orderByAsc(City::getPinyinAbbr);
        return cityMapper.selectList(qw);
    }

    @Override
    public City createCity(City city) {
        if (city.getStatus() == null) city.setStatus(0);
        if (city.getSortOrder() == null) city.setSortOrder(99);
        city.setCreateTime(LocalDateTime.now());
        city.setUpdateTime(LocalDateTime.now());
        cityMapper.insert(city);
        return city;
    }

    @Override
    public City updateCity(City city) {
        city.setUpdateTime(LocalDateTime.now());
        cityMapper.updateById(city);
        return cityMapper.selectById(city.getId());
    }

    @Override
    public void updateCityStatus(Long id, Integer status) {
        LambdaUpdateWrapper<City> uw = new LambdaUpdateWrapper<>();
        uw.eq(City::getId, id)
          .set(City::getStatus, status)
          .set(City::getUpdateTime, LocalDateTime.now());
        cityMapper.update(null, uw);
    }

    @Override
    public void deleteCity(Long id) {
        cityMapper.deleteById(id);
    }
}
