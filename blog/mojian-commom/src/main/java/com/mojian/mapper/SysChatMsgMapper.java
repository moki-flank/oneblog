package com.mojian.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mojian.entity.ChatMsg;
import com.mojian.vo.chat.ChatSendMsgVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysChatMsgMapper extends BaseMapper<ChatMsg> {

    IPage<ChatSendMsgVo> getChatMsgList(Page<Object> page);
}
