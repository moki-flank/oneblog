package com.mojian.vo.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "后台管理文章详情视图对象")
public class SysArticleDetailVo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "分类id")
    private Long categoryId;

    @ApiModelProperty(value = "文章标题")
    private String title;

    @ApiModelProperty(value = "文章封面地址")
    private String cover;

    @ApiModelProperty(value = "文章简介")
    private String summary;

    @ApiModelProperty(value = "文章内容")
    private String content;

    @ApiModelProperty(value = "文章内容md格式")
    private String contentMd;

    @ApiModelProperty(value = "阅读方式 0无需验证 1：评论阅读 2：点赞阅读 3：扫码阅读")
    private Integer readType;

    @ApiModelProperty(value = "是否置顶 0否 1是")
    private Integer isStick;

    @ApiModelProperty(value = "是否发布 0：下架 1：发布")
    private Integer status;

    @ApiModelProperty(value = "是否原创  0：转载 1:原创")
    private Integer isOriginal;

    @ApiModelProperty(value = "是否首页轮播")
    private Integer isCarousel;

    @ApiModelProperty(value = "是否推荐")
    private Integer isRecommend;

    @ApiModelProperty(value = "转载地址")
    private String originalUrl;

    @ApiModelProperty(value = "关键词")
    private String keywords;

    @ApiModelProperty(value = "标签集合")
    private List<Integer> tagIds;
}
