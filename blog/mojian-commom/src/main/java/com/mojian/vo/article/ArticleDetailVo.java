package com.mojian.vo.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import com.mojian.vo.tag.TagListVo;
import com.mojian.entity.SysCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value = "文章详情视图对象")
public class ArticleDetailVo {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String contentMd;
    private String content;

    @ApiModelProperty(value = "封面")
    private String cover;

    @ApiModelProperty(value = "阅读量")
    private Integer quantity;

    @ApiModelProperty(value = "阅读方式")
    private Integer readType;

    @ApiModelProperty(value = "评论数量")
    private Integer commentNum;

    @ApiModelProperty(value = "点赞数量")
    private Integer likeNum;

    @ApiModelProperty(value = "是否原创")
    private Integer isOriginal;

    @ApiModelProperty(value = "转载地址")
    private String originalUrl;

    @ApiModelProperty(value = "是否点赞")
    private Boolean isLike;

    @ApiModelProperty(value = "分类")
    private SysCategory category;

    @ApiModelProperty(value = "Ai生成的简短描述")
    private String aiDescribe;

    @ApiModelProperty(value = "标签列表")
    private List<TagListVo> tags;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD)
    private LocalDateTime createTime;
}
