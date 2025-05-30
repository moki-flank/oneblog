package com.mojian.vo.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "分类视图对象")
public class CategoryListVo {

    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "文章列表")
    private List<ArticleListVo> posts;
}
