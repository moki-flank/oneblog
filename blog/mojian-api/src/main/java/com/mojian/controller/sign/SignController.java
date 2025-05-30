package com.mojian.controller.sign;

import com.mojian.common.Result;
import com.mojian.service.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: quequnlong
 * @date: 2025/2/8
 * @description:
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sign")
@Api(tags = "门户-签到")
public class SignController {

    private final SignService signService;

    @GetMapping("/")
    @ApiOperation(value = "签到")
    public Result<Void> sign() {
        signService.sign();
        return Result.success();
    }

    @GetMapping("/isSignedToday")
    @ApiOperation(value = "是否签到")
    public Result<Boolean> isSignedToday() {
        return Result.success(signService.isSignedToday());
    }

    @GetMapping("/getSignDays")
    @ApiOperation(value = "获取累计天数和连续天数")
    public Result<Map<String,Object>> getSignDays() {
        Map<String,Object> map = new HashMap<>();
        map.put("totalDays",signService.getCumulativeSignDays());
        map.put("continuousDays",signService.getConsecutiveSignDays());
        return Result.success(map);
    }


}
