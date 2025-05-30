package com.mojian.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.mojian.common.RedisConstants;
import com.mojian.entity.SysArticle;
import com.mojian.entity.SysCategory;
import com.mojian.service.ArticleService;
import com.mojian.utils.IpUtil;
import com.mojian.utils.RedisUtil;
import com.mojian.vo.article.ArchiveListVo;
import com.mojian.vo.article.ArticleDetailVo;
import com.mojian.vo.article.ArticleListVo;
import com.mojian.vo.article.CategoryListVo;
import com.mojian.mapper.SysArticleMapper;
import com.mojian.mapper.SysCategoryMapper;
import com.mojian.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final SysArticleMapper sysArticleMapper;

    private final SysCategoryMapper sysCategoryMapper;
    private final RedisUtil redisUtil;

    @Override
    public IPage<ArticleListVo> getArticleList(Integer tagId, Integer categoryId, String keyword) {
        return sysArticleMapper.getArticleListApi(PageUtil.getPage(), tagId, categoryId, keyword);
    }

    @Override
    public ArticleDetailVo getArticleDetail(Long id) {
        ArticleDetailVo detailVo = sysArticleMapper.getArticleDetail(id);
        // 判断是否点赞
        Object userId = StpUtil.getLoginIdDefaultNull();
        if (userId != null) {
           detailVo.setIsLike(sysArticleMapper.getUserIsLike(id, Integer.parseInt(userId.toString())));
        }

        //添加阅读量
        String ip = IpUtil.getIp();
        ThreadUtil.execAsync(() -> {
            Map<Object, Object> map = redisUtil.hGetAll(RedisConstants.ARTICLE_QUANTITY);
            List<String> ipList = (List<String> ) map.get(id.toString());
            if (ipList != null) {
                if (!ipList.contains(ip)) {
                    ipList.add(ip);
                }
            }else {
                ipList = new ArrayList<>();
                ipList.add(ip);
            }
            map.put(id.toString(),ipList);
            redisUtil.hSetAll(RedisConstants.ARTICLE_QUANTITY,map);
        });
        return detailVo;
    }

    @Override
    public List<ArchiveListVo> getArticleArchive() {

        List<ArchiveListVo> list = new ArrayList<>();

        List<Integer> years = sysArticleMapper.getArticleArchive();
        for (Integer year : years) {
            List<ArticleListVo> articleListVos = sysArticleMapper.getArticleByYear(year);
            list.add(new ArchiveListVo(year, articleListVos));
        }
        return list;
    }

    @Override
    public List<CategoryListVo> getArticleCategories() {
        return sysCategoryMapper.getArticleCategories();
    }

    @Override
    public List<ArticleListVo> getCarouselArticle() {
        return getArticlesByCondition(SysArticle::getIsCarousel);
    }

    @Override
    public List<ArticleListVo> getRecommendArticle() {
        return getArticlesByCondition(SysArticle::getIsRecommend);
    }

    @Override
    public Boolean like(Long articleId) {
        // 判断是否点赞
        int userId = StpUtil.getLoginIdAsInt();
        Boolean isLike = sysArticleMapper.getUserIsLike(articleId, userId);
        if (isLike) {
            // 点过则取消点赞
            sysArticleMapper.unLike(articleId, userId);
        }else {
            sysArticleMapper.like(articleId, userId);
        }
        return true;
    }

    @Override
    public List<SysCategory> getCategoryAll() {
        return sysCategoryMapper.selectList(null);
    }

    private List<ArticleListVo> getArticlesByCondition(SFunction<SysArticle, Object> conditionField) {
        LambdaQueryWrapper<SysArticle> wrapper = new LambdaQueryWrapper<SysArticle>()
                .select(SysArticle::getId, SysArticle::getTitle, SysArticle::getCover, SysArticle::getCreateTime)
                .orderByDesc(SysArticle::getCreateTime)
                .eq(conditionField, 1);

        List<SysArticle> sysArticles = sysArticleMapper.selectList(wrapper);

        if (sysArticles == null || sysArticles.isEmpty()) {
            return Collections.emptyList();
        }

        return sysArticles.stream().map(item -> ArticleListVo.builder()
                .id(item.getId())
                .cover(item.getCover())
                .title(item.getTitle())
                .createTime(item.getCreateTime())
                .build()).collect(Collectors.toList());
    }
}
