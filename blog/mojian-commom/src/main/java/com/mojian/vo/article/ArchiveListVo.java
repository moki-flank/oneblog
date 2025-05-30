package com.mojian.vo.article;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "归档视图对象")
public class ArchiveListVo {

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "文章列表")
    private List<ArticleListVo> posts;
}
