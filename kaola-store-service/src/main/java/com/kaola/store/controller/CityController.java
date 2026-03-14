package com.kaola.store.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.store.model.entity.City;
import com.kaola.store.service.CityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 城市控制器（小程序端）
 *
 * @author Kaola Team
 */
@Slf4j
@RestController
@RequestMapping("/city")
public class CityController {

    @Autowired
    private CityService cityService;

    /**
     * 获取所有城市列表
     *
     * @return 城市列表
     */
    @GetMapping("/list")
    public Result<List<City>> getCityList() {
        log.info("获取城市列表");

        try {
            List<City> cities = cityService.getAllCities();
            return Result.success(cities);
        } catch (Exception e) {
            log.error("获取城市列表失败", e);
            return Result.error("获取城市列表失败");
        }
    }

    /**
     * 搜索城市
     *
     * @param keyword 搜索关键词
     * @return 城市列表
     */
    @GetMapping("/search")
    public Result<List<City>> searchCities(@RequestParam String keyword) {
        log.info("搜索城市，关键词: {}", keyword);

        try {
            List<City> cities = cityService.searchCities(keyword);
            return Result.success(cities);
        } catch (Exception e) {
            log.error("搜索城市失败", e);
            return Result.error("搜索城市失败");
        }
    }
}
