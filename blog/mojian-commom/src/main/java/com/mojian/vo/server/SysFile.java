package com.mojian.vo.server;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "服务器文件信息")
public class SysFile {

    @ApiModelProperty(value = "盘符路径")
    private String dirName;

    @ApiModelProperty(value = "文件系统")
    private String typeName;

    @ApiModelProperty(value = "总大小")
    private long total;

    @ApiModelProperty(value = "剩余大小")
    private long free;

    @ApiModelProperty(value = "已用大小")
    private long used;

    @ApiModelProperty(value = "使用率")
    private double usage;
}
