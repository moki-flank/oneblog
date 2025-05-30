package com.mojian.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.common.Result;
import com.mojian.service.SysUserService;
import com.mojian.vo.user.OnlineUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: quequnlong
 * @date: 2025/1/3
 * @description:
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/monitor/online")
@Api(tags = "在线用户")
public class OnlineUserController {

    private final SysUserService sysUserService;

    @GetMapping("/list")
    @ApiOperation(value = "获取在线用户列表")
    public Result<IPage<OnlineUserVo>> getOnlineUserList(String username) {
        return Result.success(sysUserService.getOnlineUserList(username));
    }

    @ApiOperation(value = "强制踢出")
    @GetMapping("/forceLogout/{token}")
    @SaCheckPermission("monitor:online:forceLogout")
    public Result<Void> forceLogout(@PathVariable String token) {
        StpUtil.logoutByTokenValue(token);
        return Result.success();
    }

}
