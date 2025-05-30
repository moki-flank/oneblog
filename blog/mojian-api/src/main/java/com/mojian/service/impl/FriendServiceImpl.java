package com.mojian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mojian.service.FriendService;
import com.mojian.entity.SysFriend;
import com.mojian.enums.FriendStatusEnum;
import com.mojian.exception.ServiceException;
import com.mojian.mapper.SysFriendMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final SysFriendMapper friendMapper;

    @Override
    public List<SysFriend> getFriendList() {
        return friendMapper.selectList(new LambdaQueryWrapper<SysFriend>()
                .select(SysFriend::getId,SysFriend::getName,SysFriend::getInfo,SysFriend::getAvatar
                        ,SysFriend::getUrl)
                .eq(SysFriend::getStatus, FriendStatusEnum.UP.getCode())
                .orderByAsc(SysFriend::getSort));
    }

    @Override
    public Boolean apply(SysFriend sysFriend) {
        SysFriend obj = friendMapper.selectOne(new LambdaQueryWrapper<SysFriend>()
                .eq(SysFriend::getUrl, sysFriend.getUrl()));
        if (ObjectUtils.isNotEmpty(obj)) {
            throw new ServiceException("申请友链失败，该网站已存在");
        }

        sysFriend.setStatus(FriendStatusEnum.APPLY.getCode());
        friendMapper.insert(sysFriend);

        return true;
    }
}
