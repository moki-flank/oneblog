package com.mojian.service;

import com.mojian.entity.SysMessage;

import java.util.List;


public interface MessageService {

    /**
     * 获取留言列表
     * @return
     */
    List<SysMessage> getMessageList();

    /**
     * 添加留言
     * @param sysMessage
     * @return
     */
    Boolean add(SysMessage sysMessage);
}
