package com.mojian.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.annotation.OperationLogger;
import com.mojian.common.Result;
import com.mojian.entity.SysDictData;
import com.mojian.service.SysDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Api(tags = "字典数据管理")
@RestController
@RequestMapping("/sys/dictData")
@RequiredArgsConstructor
public class SysDictDataController {

    private final SysDictDataService sysDictDataService;

    @GetMapping("list")
    @ApiOperation(value = "获取字典数据列表")
    public Result<IPage<SysDictData>> listDictData(Long dictId) {
        return Result.success(sysDictDataService.listDictData(dictId));
    }

    @GetMapping("getDiceData/{dictType}")
    @ApiOperation(value = "获取字典数据列表")
    public Result<Map<String, Map<String, Object>>> getDiceDataByDictType(@PathVariable List<String> dictType) {
        return Result.success(sysDictDataService.getDiceDataByDictType(dictType));
    }

    @PostMapping("add")
    @ApiOperation(value = "新增字典数据")
    @OperationLogger(value = "新增字典数据")
    @SaCheckPermission("sys:dict:add")
    public Result<Void> addDictData(@RequestBody SysDictData dictData) {
        sysDictDataService.addDictData(dictData);
        return Result.success();
    }

    @PutMapping("update")
    @ApiOperation(value = "修改字典数据")
    @OperationLogger(value = "修改字典数据")
    @SaCheckPermission("sys:dict:update")
    public Result<Void> updateDictData(@RequestBody SysDictData dictData) {
        sysDictDataService.updateDictData(dictData);
        return Result.success();
    }

    @DeleteMapping("/delete/{ids}")
    @ApiOperation(value = "删除字典数据")
    @OperationLogger(value = "删除字典数据")
    @SaCheckPermission("sys:dict:delete")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        sysDictDataService.removeBatchByIds(ids);
        return Result.success();
    }
}
