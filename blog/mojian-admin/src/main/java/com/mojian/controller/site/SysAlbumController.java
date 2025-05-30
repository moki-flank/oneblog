package com.mojian.controller.site;

import java.util.List;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.mojian.entity.SysAlbum;
import com.mojian.service.SysAlbumService;
import com.mojian.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 相册 控制器
 */
@RestController
@RequestMapping("/sys/album")
@RequiredArgsConstructor
@Api(tags = "相册管理")
public class SysAlbumController {

    private final SysAlbumService sysAlbumService;

    @GetMapping("/list")
    @ApiOperation(value = "获取相册列表")
    public Result<IPage<SysAlbum>> selectPage(SysAlbum sysAlbum) {
        return Result.success(sysAlbumService.selectPage(sysAlbum));
    }

    @GetMapping("/all")
    @ApiOperation(value = "获取所有相册列表")
    public Result<List<SysAlbum>> selectList(SysAlbum sysAlbum) {
        return Result.success(sysAlbumService.selectList(sysAlbum));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取相册详情")
    public Result<SysAlbum> getInfo(@PathVariable("id") Long id) {
        return Result.success(sysAlbumService.getById(id));
    }

    @PostMapping("/add")
    @SaCheckPermission("sys:album:add")
    @ApiOperation(value = "添加相册")
    public Result<Object> add(@RequestBody SysAlbum sysAlbum) {
        return Result.success(sysAlbumService.insert(sysAlbum));
    }

    @PutMapping("/update")
    @SaCheckPermission("sys:album:update")
    @ApiOperation(value = "修改相册")
    public Result<Object> edit(@RequestBody SysAlbum sysAlbum) {
        return Result.success(sysAlbumService.update(sysAlbum));
    }

    @DeleteMapping("/delete/{ids}")
    @SaCheckPermission("sys:album:delete")
    @ApiOperation(value = "删除相册")
    public Result<Object> remove(@PathVariable List<Long> ids) {
        return Result.success(sysAlbumService.deleteByIds(ids));
    }
}
