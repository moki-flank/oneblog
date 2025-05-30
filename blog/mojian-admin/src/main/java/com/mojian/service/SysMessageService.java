package com.mojian.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mojian.entity.SysMessage;

/**
 * @author: quequnlong
 * @date: 2025/1/2
 * @description:
 */
public interface SysMessageService extends IService<SysMessage> {

    /**
     * 获取消息列表
     * @return
     */
    Page<SysMessage> selectList();



}
