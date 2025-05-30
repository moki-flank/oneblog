package com.mojian.vo.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value = "评论列表视图对象")
public class CommentListVo {

    @ApiModelProperty(value = "评论主键ID，自增唯一标识")
    private Integer id;

    @ApiModelProperty(value = "关联的文章ID，表明该评论所属的文章")
    private Integer articleId;

    @ApiModelProperty(value = "关联的文章标题")
    private String articleTitle;

    @ApiModelProperty(value = "评论父级id")
    private Integer parentId;

    @ApiModelProperty(value = "评论用户id")
    private Integer userId;

    @ApiModelProperty(value = "发表评论的用户ID")
    private String nickname;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "回复人id")
    private String replyNickname;

    @ApiModelProperty(value = "评论内容，使用utf8mb4字符集以支持更多字符类型")
    private String content;

    @ApiModelProperty(value = "是否置顶")
    private Integer isStick;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "ip来源")
    private String ipSource;

    @ApiModelProperty(value = "浏览器")
    private String browser;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "子评论")
    private List<CommentListVo> children;
}
