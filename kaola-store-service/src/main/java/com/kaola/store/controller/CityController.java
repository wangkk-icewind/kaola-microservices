package com.kaola.store.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.store.model.entity.City;
import com.kaola.store.service.CityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/city")
public class CityController {

    @Autowired
    private CityService cityService;

    /** 获取城市列表，status=1 只返回上线城市，不传则返回全部 */
    @GetMapping("/list")
    public Result<List<City>> getCityList(@RequestParam(required = false) Integer status) {
        return Result.success(cityService.getCities(status));
    }

    @GetMapping("/search")
    public Result<List<City>> searchCities(@RequestParam String keyword) {
        return Result.success(cityService.searchCities(keyword));
    }
}
