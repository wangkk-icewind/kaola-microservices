package com.kaola.store.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.store.model.entity.City;
import com.kaola.store.service.CityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/city")
public class AdminCityController {

    @Autowired
    private CityService cityService;

    @GetMapping("/list")
    public Result<List<City>> list(@RequestParam(required = false) Integer status) {
        return Result.success(cityService.getCities(status));
    }

    @PostMapping("/create")
    public Result<City> create(@RequestBody City city) {
        return Result.success(cityService.createCity(city));
    }

    @PutMapping("/update")
    public Result<City> update(@RequestBody City city) {
        return Result.success(cityService.updateCity(city));
    }

    @PutMapping("/updateStatus")
    public Result<Void> updateStatus(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        Integer status = Integer.valueOf(body.get("status").toString());
        cityService.updateCityStatus(id, status);
        return Result.success(null);
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam Long id) {
        cityService.deleteCity(id);
        return Result.success(null);
    }
}
