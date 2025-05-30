package com.mojian.vo.server;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "服务器信息")
public class SysInfo {

    @ApiModelProperty(value = "服务器名称")
    private String computerName;

    @ApiModelProperty(value = "服务器IP")
    private String computerIp;

    @ApiModelProperty(value = "操作系统")
    private String osName;

    @ApiModelProperty(value = "系统架构")
    private String osArch;

    @ApiModelProperty(value = "项目路径")
    private String userDir;
}
