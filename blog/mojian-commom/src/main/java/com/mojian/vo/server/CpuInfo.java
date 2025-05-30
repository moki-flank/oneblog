package com.mojian.vo.server;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "服务器cpu信息")
public class CpuInfo {

    @ApiModelProperty(value = "cpu核数")
    private int cpuNum;

    @ApiModelProperty(value = "cpu用户使用率")
    private double used;

    @ApiModelProperty(value = "cpu系统使用率")
    private double sys;

    @ApiModelProperty(value = "当前空闲率")
    private double free;
}
