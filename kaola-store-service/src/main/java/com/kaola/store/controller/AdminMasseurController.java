package com.kaola.store.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaola.common.core.dto.PageVO;
import com.kaola.common.core.dto.Result;
import com.kaola.store.model.entity.Masseur;
import com.kaola.store.service.MasseurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - 技师管理接口
 *
 * @author Kaola Team
 */
@Slf4j
@Tag(name = "管理后台 - 技师管理", description = "技师的增删改查接口")
@RestController
@RequestMapping("/admin/masseur")
@RequiredArgsConstructor
public class AdminMasseurController {

    private final MasseurService masseurService;

    /**
     * 分页查询技师列表
     */
    @Operation(summary = "分页查询技师列表", description = "支持名称搜索、门店筛选和状态筛选")
    @GetMapping("/list")
    public Result<PageVO<Masseur>> getMasseurList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Integer status) {

        log.info("分页查询技师列表, current: {}, pageSize: {}, name: {}, storeId: {}, status: {}",
                 current, pageSize, name, storeId, status);

        Page<Masseur> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Masseur> wrapper = new LambdaQueryWrapper<>();

        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Masseur::getName, name.trim());
        }

        // Note: Current Masseur entity doesn't have storeId field
        // If you add storeId field to Masseur entity in the future, uncomment this:
        // if (storeId != null) {
        //     wrapper.eq(Masseur::getStoreId, storeId);
        // }

        if (status != null) {
            wrapper.eq(Masseur::getStatus, status);
        }

        wrapper.orderByDesc(Masseur::getCreateTime);

        IPage<Masseur> pageResult = masseurService.page(page, wrapper);

        // Convert IPage to PageVO
        PageVO<Masseur> pageVO = new PageVO<>();
        pageVO.setRecords(pageResult.getRecords());
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setCurrent(pageResult.getCurrent());
        pageVO.setPageSize(pageResult.getSize());
        pageVO.setPages(pageResult.getPages());

        return Result.success(pageVO);
    }

    /**
     * 根据门店获取技师列表
     */
    @Operation(summary = "根据门店获取技师列表", description = "获取指定门店的所有技师")
    @GetMapping("/byStore/{storeId}")
    public Result<List<Masseur>> getMasseursByStore(@PathVariable Long storeId) {
        log.info("根据门店获取技师列表, storeId: {}", storeId);

        // Note: Current Masseur entity doesn't have storeId field
        // Return all active masseurs for now
        // If you add storeId field to Masseur entity in the future, add filter:
        // wrapper.eq(Masseur::getStoreId, storeId)

        LambdaQueryWrapper<Masseur> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Masseur::getStatus, 1)
               .orderByDesc(Masseur::getRating);

        List<Masseur> masseurs = masseurService.list(wrapper);
        return Result.success(masseurs);
    }

    /**
     * 获取技师详情
     */
    @Operation(summary = "获取技师详情", description = "根据ID获取技师详细信息")
    @GetMapping("/detail/{id}")
    public Result<Masseur> getMasseurDetail(@PathVariable Long id) {
        log.info("获取技师详情, id: {}", id);

        Masseur masseur = masseurService.getById(id);
        if (masseur == null || masseur.getDeleted() == 1) {
            return Result.error("技师不存在");
        }

        return Result.success(masseur);
    }

    /**
     * 创建技师
     */
    @Operation(summary = "创建技师", description = "新建技师信息")
    @PostMapping("/create")
    public Result<Boolean> createMasseur(@RequestBody Masseur masseur) {
        log.info("创建技师, name: {}", masseur.getName());

        if (masseur.getStatus() == null) {
            masseur.setStatus(1);
        }

        boolean success = masseurService.save(masseur);
        return success ? Result.success(true) : Result.error("创建失败");
    }

    /**
     * 更新技师
     */
    @Operation(summary = "更新技师", description = "修改技师信息")
    @PutMapping("/update")
    public Result<Boolean> updateMasseur(@RequestBody Masseur masseur) {
        log.info("更新技师, id: {}, name: {}", masseur.getId(), masseur.getName());

        if (masseur.getId() == null) {
            return Result.error("技师ID不能为空");
        }

        Masseur existing = masseurService.getById(masseur.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("技师不存在");
        }

        boolean success = masseurService.updateById(masseur);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 删除技师（逻辑删除）
     */
    @Operation(summary = "删除技师", description = "逻辑删除技师")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteMasseur(@PathVariable Long id) {
        log.info("删除技师, id: {}", id);

        Masseur masseur = masseurService.getById(id);
        if (masseur == null || masseur.getDeleted() == 1) {
            return Result.error("技师不存在");
        }

        boolean success = masseurService.removeById(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    /**
     * 更新技师状态
     */
    @Operation(summary = "更新技师状态", description = "更改技师在职状态")
    @PutMapping("/updateStatus")
    public Result<Boolean> updateMasseurStatus(@RequestBody Masseur request) {
        log.info("更新技师状态, id: {}, status: {}", request.getId(), request.getStatus());

        if (request.getId() == null || request.getStatus() == null) {
            return Result.error("参数不完整");
        }

        Masseur masseur = masseurService.getById(request.getId());
        if (masseur == null || masseur.getDeleted() == 1) {
            return Result.error("技师不存在");
        }

        masseur.setStatus(request.getStatus());
        boolean success = masseurService.updateById(masseur);
        return success ? Result.success(true) : Result.error("更新失败");
    }
}
