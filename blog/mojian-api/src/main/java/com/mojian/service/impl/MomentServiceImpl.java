package com.mojian.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.mapper.SysMomentMapper;
import com.mojian.service.MomentService;
import com.mojian.utils.PageUtil;
import com.mojian.vo.moment.MomentPageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author: quequnlong
 * @date: 2025/2/5
 * @description:
 */
@Service
@RequiredArgsConstructor
public class MomentServiceImpl implements MomentService {

    private final SysMomentMapper baseMapper;

    @Override
    public IPage<MomentPageVo> getMomentList() {
        return baseMapper.selectPage(PageUtil.getPage());
    }
}
