package com.mojian.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mojian.common.Constants;
import com.mojian.common.ResultCode;
import com.mojian.dto.article.ArticleQueryDto;
import com.mojian.entity.SysArticle;
import com.mojian.entity.SysTag;
import com.mojian.exception.ServiceException;
import com.mojian.mapper.SysArticleMapper;
import com.mojian.mapper.SysTagMapper;
import com.mojian.service.SysArticleService;
import com.mojian.utils.AiUtil;
import com.mojian.utils.PageUtil;
import com.mojian.vo.article.ArticleListVo;
import com.mojian.vo.article.SysArticleDetailVo;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysArticleServiceImpl extends ServiceImpl<SysArticleMapper, SysArticle> implements SysArticleService {

    private final SysTagMapper sysTagMapper;

    private final AiUtil aiUtil;

    @Override
    public IPage<ArticleListVo> selectPage(ArticleQueryDto articleQueryDto) {
        return baseMapper.selectPageList(PageUtil.getPage(), articleQueryDto);
    }

    @Override
    public SysArticleDetailVo detail(Integer id) {
        SysArticle sysArticle = baseMapper.selectById(id);

        SysArticleDetailVo sysArticleDetailVo = new SysArticleDetailVo();
        BeanUtils.copyProperties(sysArticle, sysArticleDetailVo);

        //获取标签
        List<Integer> tags = sysTagMapper.getTagIdsByArticleId(id);
        sysArticleDetailVo.setTagIds(tags);
        return sysArticleDetailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean add(SysArticleDetailVo sysArticle) {

        SysArticle obj = new SysArticle();
        BeanUtils.copyProperties(sysArticle, obj);

        obj.setUserId(StpUtil.getLoginIdAsLong());
        baseMapper.insert(obj);

        //添加标签
        sysTagMapper.addArticleTagRelations(obj.getId(), sysArticle.getTagIds());

        ThreadUtil.execAsync(() -> {
            String res = aiUtil.send(obj.getContent() + "请提供一段简短的介绍描述该文章的内容");
            if (StringUtils.isNotBlank(res)) {
                obj.setAiDescribe(res);
                baseMapper.updateById(obj);
            }
        });
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(SysArticleDetailVo sysArticle) {

        SysArticle obj = new SysArticle();
        BeanUtils.copyProperties(sysArticle, obj);

        //没有管理员权限就只能修改自己的文章
        if (!StpUtil.hasRole(Constants.ADMIN)) {
            SysArticle article = baseMapper.selectById(sysArticle.getId());
            if (article.getUserId() != StpUtil.getLoginIdAsLong()) {
                throw new ServiceException("只能修改自己的文章");
            }
        }

        baseMapper.updateById(obj);

        //先删除标签在新增标签
        sysTagMapper.deleteArticleTagsByArticleIds(Collections.singletonList(obj.getId()));
        //添加标签
        sysTagMapper.addArticleTagRelations(obj.getId(), sysArticle.getTagIds());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(List<Long> ids) {

        //没有管理员权限就只能删除自己的文章
        if (!StpUtil.hasRole(Constants.ADMIN)) {
            List<SysArticle> sysArticles = baseMapper.selectBatchIds(ids);
            for (SysArticle sysArticle : sysArticles) {
                if (sysArticle.getUserId() != StpUtil.getLoginIdAsLong()) {
                    throw new RuntimeException("只能删除自己的文章");
                }
            }
        }

        baseMapper.deleteBatchIds(ids);
        sysTagMapper.deleteArticleTagsByArticleIds(ids);
        return true;
    }


    @Override
    public void reptile(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            Elements title  = document.getElementsByClass("title-article");
            Elements tags  = document.getElementsByClass("tag-link");
            Elements content  = document.getElementsByClass("article_content");
            if (StringUtils.isBlank(content.toString())) {
                throw new ServiceException(ResultCode.CRAWLING_ARTICLE_FAILED.getDesc());
            }

            //爬取的是HTML内容，需要转成MD格式的内容
            String newContent = content.get(0).toString().replaceAll("<code>", "<code class=\"lang-java\">");
            String markdown = FlexmarkHtmlConverter.builder(new MutableDataSet()).build().convert(newContent)
                    .replace("lang-java","java");

            SysArticle entity = SysArticle.builder().userId(StpUtil.getLoginIdAsLong()).contentMd(markdown)
                    .isOriginal(Constants.NO).originalUrl(url)
                    .title(title.get(0).text()).cover("https://api.btstu.cn/sjbz/api.php?lx=dongman&format=images").content(newContent).build();

            baseMapper.insert(entity);
            //为该文章添加标签
            List<Integer> tagIds = new ArrayList<>();
            tags.forEach(item ->{
                String tag = item.text();
                SysTag result = sysTagMapper.selectOne(new LambdaQueryWrapper<SysTag>().eq(SysTag::getName,tag ));
                if (result == null){
                    result = SysTag.builder().name(tag).build();
                    sysTagMapper.insert(result);
                }
                tagIds.add(result.getId());
            });
            sysTagMapper.addArticleTagRelations(entity.getId(),tagIds);

            System.out.println("文章抓取成功，内容为:" + JSON.toJSONString(entity));
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
