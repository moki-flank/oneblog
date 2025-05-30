package com.mojian.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mojian.vo.dashboard.ContributionData;
import com.mojian.dto.article.ArticleQueryDto;
import com.mojian.vo.article.ArticleDetailVo;
import com.mojian.vo.article.ArticleListVo;
import com.mojian.entity.SysArticle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 文章 Mapper接口
 */
@Mapper
public interface SysArticleMapper extends BaseMapper<SysArticle> {

    /**
     * 门户-获取文章列表
     * @param page
     * @param tagId
     * @param categoryId
     * @return
     */
    IPage<ArticleListVo> getArticleListApi(@Param("page") Page<Object> page,@Param("tagId") Integer tagId,
                                           @Param("categoryId")Integer categoryId, @Param("keyword")String keyword);

    /**
     * 获取文章详情
     * @param id
     * @return
     */
    ArticleDetailVo getArticleDetail(Long id);

    /**
     * 获取文章归档
     * @return
     */
    List<Integer> getArticleArchive();

    /**
     * 获取文章列表按年分组
     * @param year
     * @return
     */
    List<ArticleListVo> getArticleByYear(Integer year);

    /**
     * 自定义分页查询
     * @param page
     * @param articleQueryDto
     * @return
     */
    IPage<ArticleListVo> selectPageList(@Param("page") Page<Object> page, @Param("query") ArticleQueryDto articleQueryDto);

    /**
     * 获取今年贡献度
     * @return
     */
    List<ContributionData> getThisYearContributionData();

    /**
     * 获取用户是否文章点赞
     * @param articleId
     * @param userId
     * @return
     */
    Boolean getUserIsLike(@Param("articleId") Long articleId, @Param("userId") int userId);

    /**
     * 文章取消点赞
     * @param articleId
     * @param userId
     */
    void unLike(@Param("articleId")Long articleId, @Param("userId")int userId);

    /**
     * 文章点赞
     * @param articleId
     * @param userId
     */
    void like(@Param("articleId")Long articleId,@Param("userId")int userId);

    @MapKey("name")
    List<Map<String, Integer>> selectCountByCategory();

    IPage<ArticleListVo> selectMyLike(@Param("page")Page<Object> page, @Param("userId")long userId);

    IPage<ArticleListVo> selectMyArticle(@Param("page")Page<Object> page,@Param("article") SysArticle article);

    void updateBatchQuantity(@Param("articles") List<SysArticle> articles);
}
