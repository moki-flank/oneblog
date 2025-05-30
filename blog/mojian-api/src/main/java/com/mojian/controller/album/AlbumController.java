package com.mojian.controller.album;

import com.mojian.annotation.AccessLimit;
import com.mojian.common.Result;
import com.mojian.entity.SysAlbum;
import com.mojian.entity.SysPhoto;
import com.mojian.service.AlbumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: quequnlong
 * @date: 2025/2/7
 * @description:
 */
@RestController
@RequestMapping("/api/album")
@RequiredArgsConstructor
@Api(tags = "门户-相册管理")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/list")
    @ApiOperation(value = "获取相册列表")
    public Result<List<SysAlbum>> getAlbumList() {
        return Result.success(albumService.getAlbumList());
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取相册详情")
    public Result<SysAlbum> detail(@PathVariable Long id) {
        return Result.success(albumService.detail(id));
    }

    @GetMapping("/photos/{albumId}")
    @ApiOperation(value = "获取照片列表")
    public Result<List<SysPhoto>> getPhotos(@PathVariable Long albumId) {
        return Result.success(albumService.getPhotos(albumId));
    }

    @AccessLimit(count = 5, time = 30)
    @GetMapping("/verify/{id}")
    @ApiOperation(value = "验证相册的密码")
    public Result<Boolean> verify(@PathVariable Long id,String password) {
        return albumService.verify(id,password) ? Result.success(true) : Result.error("密码错误");
    }
}
