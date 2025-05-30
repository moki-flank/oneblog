package com.mojian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mojian.entity.SysFriend;
import com.mojian.mapper.SysFriendMapper;
import com.mojian.service.SysFriendService;
import com.mojian.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SysFriendServiceImpl extends ServiceImpl<SysFriendMapper, SysFriend> implements SysFriendService {

    @Override
    public IPage<SysFriend> selectPage(SysFriend sysFriend) {
        LambdaQueryWrapper<SysFriend> wrapper = new LambdaQueryWrapper<SysFriend>()
                .eq(sysFriend.getName() != null, SysFriend::getName, sysFriend.getName())
                .eq(sysFriend.getStatus() != null, SysFriend::getStatus, sysFriend.getStatus());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 修改友情链接
     */
    @Override
    public boolean update(SysFriend sysFriend) {
        return updateById(sysFriend);
    }
}
