package com.mojian.vo.server;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
@ApiModel(value = "服务器信息")
public class ServerInfo {

    @ApiModelProperty(value = "cpu信息")
    private CpuInfo cpu;

    @ApiModelProperty(value = "内存信息")
    private MemInfo mem;

    @ApiModelProperty(value = "系统信息")
    private SysInfo sys;

    @ApiModelProperty(value = "JVM信息")
    private JvmInfo jvm;

    @ApiModelProperty(value = "磁盘信息")
    private List<SysFile> sysFiles;
}

