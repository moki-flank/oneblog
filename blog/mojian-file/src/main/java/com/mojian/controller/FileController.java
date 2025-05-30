package com.mojian.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.common.Constants;
import com.mojian.common.Result;
import com.mojian.entity.FileDetail;
import com.mojian.entity.SysFileOss;
import com.mojian.exception.ServiceException;
import com.mojian.service.FileDetailService;
import com.mojian.utils.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
@Api(tags = "文件管理")
@RequiredArgsConstructor
public class FileController {

    private final FileDetailService fileDetailService;

    private final FileStorageService fileStorageService;


    @SaCheckLogin
    @GetMapping("/list")
    @ApiOperation(value = "获取文件记录表列表")
    public Result<IPage<FileDetail>> list(FileDetail fileDetail) {
        return Result.success(fileDetailService.selectPage(fileDetail));
    }

    @SaCheckLogin
    @GetMapping("/getOssConfig")
    @ApiOperation(value = "获取存储平台配置")
    public Result<List<SysFileOss>> getOssConfig() {
        return Result.success(fileDetailService.getOssConfig());
    }

    @SaCheckLogin
    @PostMapping("/addOss")
    @SaCheckPermission("sys:oss:submit")
    @ApiOperation(value = "添加存储平台配置")
    public Result<Void> addOss(@RequestBody SysFileOss sysFileOss) {
        fileDetailService.addOss(sysFileOss);
        if (sysFileOss.getIsEnable() == Constants.YES) {
            fileStorageService.getProperties().setDefaultPlatform(sysFileOss.getPlatform());
        }
        return Result.success();
    }

    @SaCheckLogin
    @PutMapping("/updateOss")
    @SaCheckPermission("sys:oss:submit")
    @ApiOperation(value = "修改存储平台配置")
    public Result<Void> updateOss(@RequestBody SysFileOss sysFileOss) {
        fileDetailService.updateOss(sysFileOss);
        if (sysFileOss.getIsEnable() == Constants.YES) {
            fileStorageService.getProperties().setDefaultPlatform(sysFileOss.getPlatform());
        }
        return Result.success();
    }

    @SaCheckLogin
    @PostMapping("/upload")
    @ApiOperation(value = "上传文件")
    public Result<String> upload(MultipartFile file, String source) {
        String path = DateUtil.parseDateToStr(DateUtil.YYYYMMDD, DateUtil.getNowDate()) + "/";
        //这个source可在前端上传文件时提供，可用来区分是头像还是文章图片等
        if (StringUtils.isNotBlank(source)) {
            path = path + source + "/";
        }
        //获取文件名和后缀
        FileInfo fileInfo = fileStorageService.of(file)
                .setPath(path)
                .setSaveFilename(file.getOriginalFilename())
                .putAttr("source",source)
                .upload();

        if (fileInfo == null) {
            throw new ServiceException("上传文件失败");
        }
        return Result.success(fileInfo.getUrl());
    }

    @GetMapping("/delete")
    @ApiOperation(value = "删除文件")
    @SaCheckPermission("sys:file:delete")
    public Result<Boolean> delete(String url) {
        boolean flag = fileStorageService.delete(url);
        if (flag) {
            fileDetailService.delete(url);
        }
        return Result.success();
    }
}
