package com.kaola.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.product.dto.Result;
import com.kaola.product.dto.PageVO;
import com.kaola.product.model.entity.Store;
import com.kaola.product.repository.StoreRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - 门店管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "管理后台 - 门店管理", description = "门店的增删改查接口")
@RestController
@RequestMapping("/admin/store")
@RequiredArgsConstructor
public class AdminStoreController {

    private final StoreRepository storeRepository;

    /**
     * 分页查询门店列表
     */
    @Operation(summary = "分页查询门店列表", description = "支持名称搜索和状态筛选")
    @GetMapping("/list")
    public Result<PageVO<Store>> getStoreList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {

        log.info("分页查询门店列表, current: {}, pageSize: {}, name: {}, status: {}",
                 current, pageSize, name, status);

        Page<Store> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();

        // deleted条件由@TableLogic自动处理，不需要手动添加

        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Store::getName, name.trim());
        }

        if (status != null) {
            wrapper.eq(Store::getStatus, status);
        }

        wrapper.orderByDesc(Store::getCreateTime);

        IPage<Store> pageResult = storeRepository.selectPage(page, wrapper);
        PageVO<Store> pageVO = PageVO.of(pageResult);

        return Result.success(pageVO);
    }

    /**
     * 获取所有门店（不分页）
     */
    @Operation(summary = "获取所有门店", description = "获取所有营业中的门店，用于下拉选择")
    @GetMapping("/all")
    public Result<List<Store>> getAllStores() {
        log.info("获取所有门店");

        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStatus, 1)
               .orderByAsc(Store::getName);

        List<Store> stores = storeRepository.selectList(wrapper);
        return Result.success(stores);
    }

    /**
     * 获取门店详情
     */
    @Operation(summary = "获取门店详情", description = "根据ID获取门店详细信息")
    @GetMapping("/detail/{id}")
    public Result<Store> getStoreDetail(@PathVariable Long id) {
        log.info("获取门店详情, id: {}", id);

        Store store = storeRepository.selectById(id);
        if (store == null || store.getDeleted() == 1) {
            return Result.error("门店不存在");
        }

        return Result.success(store);
    }

    /**
     * 创建门店
     */
    @Operation(summary = "创建门店", description = "新建门店信息")
    @PostMapping("/create")
    public Result<Boolean> createStore(@RequestBody Store store) {
        log.info("创建门店, name: {}", store.getName());

        if (store.getStatus() == null) {
            store.setStatus(1);
        }

        int rows = storeRepository.insert(store);
        return rows > 0 ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 更新门店
     */
    @Operation(summary = "更新门店", description = "修改门店信息")
    @PutMapping("/update")
    public Result<Boolean> updateStore(@RequestBody Store store) {
        log.info("更新门店, id: {}, name: {}", store.getId(), store.getName());

        if (store.getId() == null) {
            return Result.error("门店ID不能为空");
        }

        Store existing = storeRepository.selectById(store.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("门店不存在");
        }

        int rows = storeRepository.updateById(store);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 删除门店（逻辑删除）
     */
    @Operation(summary = "删除门店", description = "逻辑删除门店")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteStore(@PathVariable Long id) {
        log.info("删除门店, id: {}", id);

        Store store = storeRepository.selectById(id);
        if (store == null || store.getDeleted() == 1) {
            return Result.error("门店不存在");
        }

        store.setDeleted(1);
        int rows = storeRepository.updateById(store);
        return rows > 0 ? Result.success(true) : Result.error("删除失败");
    }

    /**
     * 更新门店状态
     */
    @Operation(summary = "更新门店状态", description = "启用或禁用门店")
    @PutMapping("/updateStatus")
    public Result<Boolean> updateStoreStatus(@RequestBody Store request) {
        log.info("更新门店状态, id: {}, status: {}", request.getId(), request.getStatus());

        if (request.getId() == null || request.getStatus() == null) {
            return Result.error("参数不完整");
        }

        Store store = storeRepository.selectById(request.getId());
        if (store == null || store.getDeleted() == 1) {
            return Result.error("门店不存在");
        }

        store.setStatus(request.getStatus());
        int rows = storeRepository.updateById(store);
        return rows > 0 ? Result.success(true) : Result.error("更新失败");
    }
}
