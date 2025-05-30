package com.mojian.controller.chat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.common.Result;
import com.mojian.vo.chat.ChatSendMsgVo;
import com.mojian.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Api(tags = "门户-聊天管理")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/list")
    @ApiOperation(value = "获取聊天历史纪录")
    public Result<IPage<ChatSendMsgVo>> getChatMsgList() {
        return Result.success(chatService.getChatMsgList());
    }

    @PostMapping("/sendMsg")
    @ApiOperation(value = "发送消息")
    public Result<Void> sendMsg(@RequestBody ChatSendMsgVo chatSendMsgVo) {
        chatService.sendMsg(chatSendMsgVo);
        return Result.success();
    }

    @PostMapping("/recallMsg")
    @ApiOperation(value = "撤回消息")
    public Result<Void> recallMsg(@RequestBody ChatSendMsgVo chatSendMsgVo) {
        chatService.recallMsg(chatSendMsgVo);
        return Result.success();
    }

}
