package com.mojian.vo.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IndexVo {

    @ApiModelProperty(value = "文章数量")
    private Long articleCount;

    @ApiModelProperty(value = "用户数量")
    private Long userCount;

    @ApiModelProperty(value = "留言数量")
    private Long messageCount ;

    @ApiModelProperty(value = "访问量")
    private int visitCount;

    @ApiModelProperty(value = "贡献度")
    private List<ContributionData> contributionData;


}
