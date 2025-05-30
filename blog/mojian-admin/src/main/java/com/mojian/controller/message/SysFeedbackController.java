package com.mojian.controller.message;

import java.util.List;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mojian.dto.feedback.SysFeedbackQueryDto;
import com.mojian.vo.feedback.SysFeedbackVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.mojian.entity.SysFeedback;
import com.mojian.service.SysFeedbackService;
import com.mojian.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 反馈表 控制器
 */
@RestController
@RequestMapping("/sys/feedback")
@RequiredArgsConstructor
@Api(tags = "反馈管理")
public class SysFeedbackController {

    private final SysFeedbackService sysFeedbackService;


    @GetMapping("/list")
    @ApiOperation(value = "获取反馈列表")
    public Result<IPage<SysFeedbackVo>> list(SysFeedbackQueryDto feedbackQueryDto) {
        return Result.success(sysFeedbackService.selectPage(feedbackQueryDto));
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加反馈")
    @SaCheckPermission("sys:feedback:add")
    public Result<Object> add(@RequestBody SysFeedback sysFeedback) {
        return Result.success(sysFeedbackService.insert(sysFeedback));
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改反馈")
    @SaCheckPermission("sys:feedback:update")
    public Result<Object> update(@RequestBody SysFeedback sysFeedback) {
        return Result.success(sysFeedbackService.update(sysFeedback));
    }

    @DeleteMapping("/delete/{ids}")
    @ApiOperation(value = "删除反馈")
    @SaCheckPermission("sys:feedback:delete")
    public Result<Object> delete(@PathVariable List<Long> ids) {
        return Result.success(sysFeedbackService.removeBatchByIds(ids));
    }
}
