package com.mojian.mapper;

import com.mojian.vo.tag.TagListVo;
import com.mojian.entity.SysTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签表 Mapper接口
 */
@Mapper
public interface SysTagMapper extends BaseMapper<SysTag> {

    List<TagListVo> getTagsApi();

    List<Integer> getTagIdsByArticleId(Integer id);

    List<TagListVo> getTagByArticleId(Long id);


    void deleteArticleTagsByArticleIds(List<Long> ids);

    void addArticleTagRelations(@Param("articleId") Long articleId, @Param("tagIds") List<Integer> tagIds);

}
