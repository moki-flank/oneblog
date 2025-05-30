package com.mojian.service.impl;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.mojian.service.MessageService;
import com.mojian.entity.SysMessage;
import com.mojian.mapper.SysMessageMapper;
import com.mojian.utils.IpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final SysMessageMapper messageMapper;

    @Override
    public List<SysMessage> getMessageList() {
        return messageMapper.selectList(null);
    }

    @Override
    public Boolean add(SysMessage sysMessage) {
        String ip = IpUtil.getIp();
        sysMessage.setIp(ip);
        sysMessage.setSource(IpUtil.getIp2region(ip));
        sysMessage.setContent(SensitiveWordHelper.replace(sysMessage.getContent()));
        messageMapper.insert(sysMessage);
        return true;
    }
}
