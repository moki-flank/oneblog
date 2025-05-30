package com.mojian.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.mojian.common.Constants;
import com.mojian.entity.SysUser;
import com.mojian.enums.ChatTypeEnum;
import com.mojian.exception.ServiceException;
import com.mojian.mapper.SysUserMapper;
import com.mojian.utils.AiUtil;
import com.mojian.utils.BeanCopyUtil;
import com.mojian.utils.IpUtil;
import com.mojian.vo.chat.ChatSendMsgVo;
import com.mojian.entity.ChatMsg;
import com.mojian.mapper.SysChatMsgMapper;
import com.mojian.service.ChatService;
import com.mojian.utils.PageUtil;
import com.mojian.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final WebSocketServer webSocketServer;

    private final SysChatMsgMapper chatMsgMapper;

    private final SysUserMapper sysUserMapper;

    private final AiUtil aiUtil;


    @Override
    public IPage<ChatSendMsgVo> getChatMsgList() {
        return chatMsgMapper.getChatMsgList(PageUtil.getPage());
    }

    @Override
    public void sendMsg(ChatSendMsgVo chatSendMsgVo) {

        //如果是文字类型的，就先敏感词过滤
        if (ChatTypeEnum.TEXT.getType().equals(chatSendMsgVo.getType())){
            chatSendMsgVo.setContent(SensitiveWordHelper.replace(chatSendMsgVo.getContent()));
        }

        ChatMsg chatMsg = BeanCopyUtil.copyObj(chatSendMsgVo, ChatMsg.class);
        chatMsg.setSenderId(StpUtil.getLoginIdAsLong());
        chatMsg.setIp(IpUtil.getIp());
        chatMsg.setLocation(IpUtil.getIp2region(chatMsg.getIp()));


        chatMsgMapper.insert(chatMsg);

        chatSendMsgVo.setId(chatMsg.getId());
        chatSendMsgVo.setLocation(IpUtil.getIp2region(chatMsg.getIp()));
        webSocketServer.sendAllMessage(JSON.toJSONString(chatSendMsgVo));

        //判断是否@拾壹小助手
        String SHINY_XIA_ASSISTANT = "@拾壹小助手";
        if (chatSendMsgVo.getContent().contains(SHINY_XIA_ASSISTANT)) {
            ThreadUtil.execAsync(() -> {
                String replaceContent = chatSendMsgVo.getContent().replace(SHINY_XIA_ASSISTANT, "");
                String aiContent = aiUtil.send(replaceContent);

                SysUser sysUser = sysUserMapper.selectById(Constants.XIAO_ASSISTANT_ID);
                ChatSendMsgVo vo = ChatSendMsgVo.builder()
                        .avatar(sysUser.getAvatar())
                        .name(sysUser.getNickname())
                        .content("@" + chatSendMsgVo.getName() + " " + aiContent)
                        .userId(Constants.XIAO_ASSISTANT_ID)
                        .type(ChatTypeEnum.TEXT.getType())
                        .build();
                ChatMsg obj = BeanCopyUtil.copyObj(vo, ChatMsg.class);
                obj.setSenderId(Constants.XIAO_ASSISTANT_ID);

                chatMsgMapper.insert(obj);
                vo.setId(obj.getId());
                webSocketServer.sendAllMessage(JSON.toJSONString(vo));
            });
        }
    }

    @Override
    public void recallMsg(ChatSendMsgVo chatSendMsgVo) {
        ChatMsg chatMsg1 = chatMsgMapper.selectById(chatSendMsgVo.getId());
        //判断发送的时间是否超过当前时间俩分钟 LocalDateTime类型
        if (chatMsg1.getCreateTime().plusMinutes(2).isBefore(LocalDateTime.now())) {
            throw new ServiceException("只能撤回俩分钟以内的消息！");
        }

        chatSendMsgVo.setIsRecalled(Boolean.TRUE);
        chatSendMsgVo.setContent("消息已撤回");

        ChatMsg chatMsg = new ChatMsg();
        BeanUtils.copyProperties(chatSendMsgVo, chatMsg);
        chatMsgMapper.updateById(chatMsg);

        webSocketServer.sendAllMessage(JSON.toJSONString(chatSendMsgVo));
    }
}
