package com.mojian.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Service;
import com.mojian.mapper.SysMomentMapper;
import com.mojian.entity.SysMoment;
import com.mojian.service.SysMomentService;
import com.mojian.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 * 说说 服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysMomentServiceImpl extends ServiceImpl<SysMomentMapper, SysMoment> implements SysMomentService {

    /**
     * 查询说说分页列表
     */
    @Override
    public IPage<SysMoment> selectPage(SysMoment sysMoment) {
        return page(PageUtil.getPage(), new LambdaQueryWrapper<SysMoment>()
                .orderByDesc(SysMoment::getCreateTime));
    }

    @Override
    public Object add(SysMoment sysMoment) {
        sysMoment.setUserId(StpUtil.getLoginIdAsLong());
        return save(sysMoment);
    }
}
