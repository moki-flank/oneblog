package com.mojian.vo.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "贡献度数据")
public class ContributionData {

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "数量")
    private Integer count;
}
