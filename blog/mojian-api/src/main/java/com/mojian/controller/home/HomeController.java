package com.mojian.controller.home;

import com.alibaba.fastjson2.JSONObject;
import com.mojian.entity.SysNotice;
import com.mojian.service.HomeService;
import com.mojian.common.Result;
import com.mojian.entity.SysWebConfig;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/webConfig")
    @ApiOperation(value = "获取网站配置")
    public Result<SysWebConfig> getWebConfig() {
        return homeService.getWebConfig();
    }

    @GetMapping("/getNotice")
    @ApiOperation(value = "获取公告")
    public Result<Map<String, List<SysNotice>>> getNotice() {
        return Result.success(homeService.getNotice());
    }

    @GetMapping("/getHotSearch/{type}")
    @ApiOperation(value = "获取热搜")
    public Result<JSONObject> getHotSearch(@PathVariable String type) {
        return Result.success(homeService.getHotSearch(type));
    }

    @GetMapping("/report")
    @ApiOperation(value = "添加访问量")
    public Result<Void> report() {
        homeService.report();
        return Result.success();
    }

}
