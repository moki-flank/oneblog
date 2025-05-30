package com.mojian.controller.site;

import java.util.List;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.mojian.entity.SysPhoto;
import com.mojian.service.SysPhotoService;
import com.mojian.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 照片 控制器
 */
@RestController
@RequestMapping("/sys/photo")
@RequiredArgsConstructor
@Api(tags = "照片管理")
public class SysPhotoController {

    private final SysPhotoService sysPhotoService;

    @GetMapping("/list")
    @ApiOperation(value = "获取照片列表")
    public Result<IPage<SysPhoto>> list(SysPhoto sysPhoto) {
        return Result.success(sysPhotoService.selectPage(sysPhoto));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取照片详情")
    public Result<SysPhoto> getInfo(@PathVariable("id") Long id) {
        return Result.success(sysPhotoService.getById(id));
    }

    @PostMapping("/add")
    @SaCheckPermission("sys:photo:add")
    @ApiOperation(value = "添加照片")
    public Result<Object> add(@RequestBody SysPhoto sysPhoto) {
        return Result.success(sysPhotoService.insert(sysPhoto));
    }

    @PutMapping("/update")
    @SaCheckPermission("sys:photo:update")
    @ApiOperation(value = "修改照片")
    public Result<Object> edit(@RequestBody SysPhoto sysPhoto) {
        return Result.success(sysPhotoService.update(sysPhoto));
    }

    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("sys:photo:delete")
    @ApiOperation(value = "删除照片")
    public Result<Object> remove(@PathVariable List<Long> ids) {
        return Result.success(sysPhotoService.deleteByIds(ids));
    }

    @PutMapping("/move/{ids}")
    @SaCheckPermission("sys:photo:move")
    @ApiOperation(value = "移动照片")
    public Result<Object> move(@PathVariable List<Long> ids, @RequestParam Long albumId) {
        return Result.success(sysPhotoService.move(ids,albumId));
    }
}
