package com.kaola.store.controller;

import com.kaola.common.core.dto.Result;
import com.kaola.store.model.vo.StoreVO;
import com.kaola.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 门店接口
 *
 * @author Kaola Team
 */
@Tag(name = "门店接口", description = "门店查询、定位等接口")
@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
@Validated
public class StoreController {

    private final StoreService storeService;

    /**
     * 获取附近门店
     *
     * @param latitude  纬度
     * @param longitude  经度
     * @param city 城市
     * @return 门店列表
     */
    @Operation(summary = "获取附近门店", description = "根据经纬度获取附近门店列表")
    @GetMapping("/nearby")
    public Result<List<StoreVO>> getNearbyStores(
            @Parameter(description = "纬度", required = false)
            @RequestParam(required = false) BigDecimal latitude,
            @Parameter(description = "经度", required = false)
            @RequestParam(required = false) BigDecimal longitude,
            @Parameter(description = "纬度(旧参数名，兼容)", required = false)
            @RequestParam(required = false) BigDecimal lat,
            @Parameter(description = "经度(旧参数名，兼容)", required = false)
            @RequestParam(required = false) BigDecimal lng,
            @Parameter(description = "城市")
            @RequestParam(required = false) String city) {
        // 兼容新旧参数名
        BigDecimal finalLat = latitude != null ? latitude : lat;
        BigDecimal finalLng = longitude != null ? longitude : lng;

        if (finalLat == null || finalLng == null) {
            return Result.error("纬度和经度不能为空");
        }

        List<StoreVO> stores = storeService.getNearbyStores(finalLat, finalLng, city);
        return Result.success(stores);
    }

    /**
     * 获取门店详情
     *
     * @param id 门店ID
     * @return 门店详情
     */
    @Operation(summary = "获取门店详情", description = "根据门店ID获取门店详细信息")
    @GetMapping("/{id}")
    public Result<StoreVO> getStoreDetail(
            @Parameter(description = "门店ID", required = true)
            @PathVariable @NotNull(message = "门店ID不能为空") Long id) {
        StoreVO store = storeService.getStoreDetail(id);
        if (store == null) {
            return Result.error("门店不存在");
        }
        return Result.success(store);
    }

    /**
     * 按城市获取门店
     *
     * @param city 城市名称
     * @return 门店列表
     */
    @Operation(summary = "按城市获取门店", description = "根据城市名称获取门店列表")
    @GetMapping("/city/{city}")
    public Result<List<StoreVO>> getStoresByCity(
            @Parameter(description = "城市名称", required = true)
            @PathVariable @NotBlank(message = "城市名称不能为空") String city) {
        List<StoreVO> stores = storeService.getStoresByCity(city);
        return Result.success(stores);
    }
}
