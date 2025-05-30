package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.io.Serializable;
/**
 * 文章
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_article")
@ApiModel(value = "文章对象 gen_table")
public class SysArticle implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

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

    @ApiModelProperty(value = "状态 0：下架 1：发布")
    private Integer status;

    @ApiModelProperty(value = "是否原创  0：转载 1:原创")
    private Integer isOriginal;

    @ApiModelProperty(value = "是否首页轮播")
    private Integer isCarousel;

    @ApiModelProperty(value = "是否推荐")
    private Integer isRecommend;

    @ApiModelProperty(value = "转载地址")
    private String originalUrl;

    @ApiModelProperty(value = "文章阅读量")
    private Integer quantity;

    @ApiModelProperty(value = "关键词")
    private String keywords;


    @ApiModelProperty(value = "Ai生成的简短描述")
    private String aiDescribe;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime updateTime;
}
