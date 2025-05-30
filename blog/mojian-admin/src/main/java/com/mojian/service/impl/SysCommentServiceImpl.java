package com.mojian.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mojian.entity.SysComment;
import com.mojian.mapper.SysCommentMapper;
import com.mojian.service.SysCommentService;
import com.mojian.utils.PageUtil;
import com.mojian.vo.comment.SysCommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author: quequnlong
 * @date: 2025/1/2
 * @description:
 */
@Service
@RequiredArgsConstructor
public class SysCommentServiceImpl extends ServiceImpl<SysCommentMapper,SysComment> implements SysCommentService {

    @Override
    public Page<SysCommentVO> selectList() {
        return baseMapper.selectPage(PageUtil.getPage());
    }
}
