package com.mojian.service;

import com.mojian.entity.SysFriend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 友情链接 服务接口
 */
public interface SysFriendService extends IService<SysFriend> {
    /**
     * 查询友情链接分页列表
     */
    IPage<SysFriend> selectPage(SysFriend sysFriend);

    /**
     * 修改友情链接
     */
    boolean update(SysFriend sysFriend);

}