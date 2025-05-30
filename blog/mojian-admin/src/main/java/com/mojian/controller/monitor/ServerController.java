package com.mojian.controller.monitor;

import com.mojian.common.Result;
import com.mojian.service.ServerService;
import com.mojian.vo.server.ServerInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor/server")
@Api(tags = "服务器监控")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;

    @GetMapping
    @ApiOperation(value = "获取服务器信息")
    public Result<ServerInfo> getServerInfo() {
        return Result.success(serverService.getServerInfo());
    }
}
