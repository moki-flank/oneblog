package com.mojian.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.mojian.service.CommentService;
import com.mojian.vo.comment.CommentListVo;
import com.mojian.entity.SysComment;
import com.mojian.mapper.SysCommentMapper;
import com.mojian.utils.IpUtil;
import com.mojian.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final SysCommentMapper sysCommentMapper;

    @Override
    public IPage<CommentListVo> getComments(Integer articleId,String sortType) {
        IPage<CommentListVo> page = sysCommentMapper.getComments(PageUtil.getPage(),articleId,sortType);
        //获取所有子评论
        page.getRecords().forEach(commentListVo -> {
            List<CommentListVo> children = sysCommentMapper.getChildrenComment(commentListVo.getId());
            commentListVo.setChildren(children);
        });
        return page;
    }

    @Override
    public void add(SysComment sysComment) {

        String ip = IpUtil.getIp();
        sysComment.setIp(ip);
        sysComment.setIpSource(IpUtil.getIp2region(ip));
        sysComment.setUserId(StpUtil.getLoginIdAsInt());
        sysComment.setContent(SensitiveWordHelper.replace(sysComment.getContent()));

        sysCommentMapper.insert(sysComment);
    }
}
