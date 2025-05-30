package com.mojian.controller.message;

import com.mojian.annotation.AccessLimit;
import com.mojian.service.MessageService;
import com.mojian.common.Result;
import com.mojian.entity.SysMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@Api(tags = "门户-留言管理")
public class MessageController {

    private final MessageService messageService;

    @AccessLimit
    @GetMapping("/list")
    @ApiOperation(value = "留言列表")
    public Result<List<SysMessage>> getMessageList() {
        return Result.success(messageService.getMessageList());
    }

    @PostMapping("/add")
    @ApiOperation(value = "发表留言")
    public Result<Boolean> add(@RequestBody SysMessage sysMessage) {
        return Result.success(messageService.add(sysMessage));
    }
}
