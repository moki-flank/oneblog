package com.mojian.controller.article;

import java.util.List;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.mojian.entity.SysTag;
import com.mojian.service.SysTagService;
import com.mojian.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 标签表 控制器
 */
@RestController
@RequestMapping("/sys/tag")
@RequiredArgsConstructor
@Api(tags = "标签管理")
public class SysTagController {

    private final SysTagService sysTagService;

    @GetMapping("/list")
    @ApiOperation(value = "标签列表")
    public Result<IPage<SysTag>> list(SysTag sysTag) {
        return Result.success(sysTagService.selectPage(sysTag));
    }

    @PostMapping
    @ApiOperation(value = "新增标签")
    @SaCheckPermission("sys:tag:add")
    public Result<Object> add(@RequestBody SysTag sysTag) {
        return Result.success(sysTagService.insert(sysTag));
    }

    @PutMapping
    @ApiOperation(value = "修改标签")
    @SaCheckPermission("sys:tag:update")
    public Result<Object> edit(@RequestBody SysTag sysTag) {
        return Result.success(sysTagService.update(sysTag));
    }

    @DeleteMapping("/delete/{ids}")
    @ApiOperation(value = "删除标签")
    @SaCheckPermission("sys:tag:delete")
    public Result<Object> remove(@PathVariable List<Integer> ids) {
        return Result.success(sysTagService.deleteByIds(ids));
    }
}
