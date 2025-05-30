package com.mojian.vo.server;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "服务器jvm信息")
public class JvmInfo {

    @ApiModelProperty(value = "当前JVM占用的总内存")
    private String total;

    @ApiModelProperty(value = "JVM最大可用内存")
    private String name;

    @ApiModelProperty(value = "JVM内存使用率")
    private String usage;

    @ApiModelProperty(value = "JDK版本")
    private String version;

    @ApiModelProperty(value = "JDK路径")
    private String home;

    @ApiModelProperty(value = "JDK启动时间")
    private String startTime;

    @ApiModelProperty(value = "JDK运行时长")
    private String runTime;
}
