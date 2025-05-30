package com.mojian.controller.dashboard;


import com.mojian.common.Result;
import com.mojian.vo.dashboard.IndexVo;
import com.mojian.service.IndexService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/sys/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IndexService indexService;

    @GetMapping
    @ApiOperation(value = "首页")
    public Result<IndexVo> index() {
        return Result.success(indexService.index());
    }


    @GetMapping("/bottom")
    @ApiOperation(value = "首页底部分类")
    public Result<List<Map<String, Integer>>> getCategories() {
        return Result.success(indexService.getCategories());
    }

}
