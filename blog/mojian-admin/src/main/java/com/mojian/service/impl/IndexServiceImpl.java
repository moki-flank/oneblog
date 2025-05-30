package com.mojian.service.impl;

import com.mojian.common.RedisConstants;
import com.mojian.vo.dashboard.ContributionData;
import com.mojian.vo.dashboard.IndexVo;
import com.mojian.mapper.SysArticleMapper;
import com.mojian.mapper.SysMessageMapper;
import com.mojian.mapper.SysUserMapper;
import com.mojian.service.IndexService;
import com.mojian.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IndexServiceImpl implements IndexService {

    private final SysUserMapper sysUserMapper;

    private final SysArticleMapper sysArticleMapper;

    private final SysMessageMapper sysMessageMapper;

    private final RedisUtil redisUtil;

    @Override
    public IndexVo index() {
        Long userCount = sysUserMapper.selectCount(null);
        Long articleCount = sysArticleMapper.selectCount(null);
        Long messageCount = sysMessageMapper.selectCount(null);

        int visitCount = 0;
        Object e = redisUtil.get(RedisConstants.BLOG_VIEWS_COUNT);
        if (e != null) {
            visitCount = Integer.parseInt(e.toString());
        }

        List<ContributionData> list = sysArticleMapper.getThisYearContributionData();

        return IndexVo.builder()
                .articleCount(articleCount)
                .userCount(userCount)
                .messageCount(messageCount)
                .visitCount(visitCount)
                .contributionData(list)
                .build();
    }

    @Override
    public List<Map<String, Integer>> getCategories() {
        List<Map<String, Integer>> list = sysArticleMapper.selectCountByCategory();
        return list;
    }
}
