package com.mojian.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mojian.annotation.OperationLogger;
import com.mojian.common.Result;
import com.mojian.entity.SysMenu;
import com.mojian.service.SysMenuService;
import com.mojian.vo.menu.RouterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/menu")
@RequiredArgsConstructor
@Api(tags = "菜单管理")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @GetMapping("/tree")
    @ApiOperation(value = "获取菜单树列表")
    public Result<List<SysMenu>> getMenuTree() {
        return Result.success(sysMenuService.getMenuTree());
    }

    @PostMapping
    @ApiOperation(value = "添加菜单")
    @OperationLogger(value = "添加菜单")
    @SaCheckPermission("sys:menu:add")
    public Result<Void> addMenu(@RequestBody SysMenu menu) {
        sysMenuService.addMenu(menu);
        return Result.success();
    }

    @PutMapping
    @ApiOperation(value = "修改菜单")
    @OperationLogger(value = "修改菜单")
    @SaCheckPermission("sys:menu:update")
    public Result<Void> updateMenu(@RequestBody SysMenu menu) {
        sysMenuService.updateMenu(menu);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除菜单")
    @OperationLogger(value = "删除菜单")
    @SaCheckPermission("sys:menu:delete")
    public Result<Void> deleteMenu(@PathVariable Integer id) {
        sysMenuService.deleteMenu(id);
        return Result.success();
    }

    @GetMapping(value = "/routers")
    @ApiOperation(value = "获取用户菜单")
    public Result<List<RouterVO>> getCurrentUserMenu() {
        return Result.success(sysMenuService.getCurrentUserMenu());
    }
}
