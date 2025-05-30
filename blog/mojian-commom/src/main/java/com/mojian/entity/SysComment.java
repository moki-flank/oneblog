package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.io.Serializable;
/**
 * 评论
 */
@Data
@TableName("sys_comment")
@ApiModel(value = "评论对象 gen_table")
public class SysComment implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "评论主键ID，自增唯一标识")
    private Integer id;

    @ApiModelProperty(value = "关联的文章ID，表明该评论所属的文章")
    private Integer articleId;

    @ApiModelProperty(value = "发表评论的用户ID")
    private Integer userId;

    @ApiModelProperty(value = "回复人id")
    private Integer replyUserId;

    @ApiModelProperty(value = "父评论ID，用于实现回复评论的层级结构，若为顶级评论则为NULL")
    private Integer parentId;

    @ApiModelProperty(value = "评论内容，使用utf8mb4字符集以支持更多字符类型")
    private String content;

    @ApiModelProperty(value = "点赞数，记录该评论获得的点赞数量")
    private Integer likeCount;

    @ApiModelProperty(value = "是否置顶")
    private Integer isStick;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "ip来源")
    private String ipSource;

    @ApiModelProperty(value = "浏览器")
    private String browser;

    @ApiModelProperty(value = "系统")
    private String system;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createTime;
}
