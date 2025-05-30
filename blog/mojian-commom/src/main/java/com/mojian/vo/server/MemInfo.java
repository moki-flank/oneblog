package com.mojian.vo.server;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "服务器内存信息")
public class MemInfo {

    @ApiModelProperty(value = "总内存")
    private long total;

    @ApiModelProperty(value = "已用内存")
    private long used;

    @ApiModelProperty(value = "剩余内存")
    private long free;

    @ApiModelProperty(value = "使用率")
    private double usage;
}
