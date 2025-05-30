package com.mojian.controller.moment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.common.Result;
import com.mojian.service.MomentService;
import com.mojian.vo.moment.MomentPageVo;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: quequnlong
 * @date: 2025/2/5
 * @description:
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/moment")
@Api(tags = "门户-说说管理")
public class MomentController {

    private final MomentService momentService;

    @GetMapping("/list")
    @Operation(description = "说说列表")
    public Result<IPage<MomentPageVo>> getMomentList() {
        return Result.success(momentService.getMomentList());
    }

}
