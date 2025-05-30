package com.mojian.vo.cache;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "缓存基本信息")
public class CacheInfoVo {

    @ApiModelProperty(value = "Redis版本")
    private String version;

    @ApiModelProperty(value = "运行模式")
    private String mode;

    @ApiModelProperty(value = "端口")
    private String port;

    @ApiModelProperty(value = "运行时间(天)")
    private String uptime;

    @ApiModelProperty(value = "连接客户端数")
    private String clients;

    @ApiModelProperty(value = "已使用内存")
    private String usedMemory;

    @ApiModelProperty(value = "内存配置")
    private String maxmemory;

    @ApiModelProperty(value = "AOF是否开启")
    private String aofEnabled;

    @ApiModelProperty(value = "RDB是否成功")
    private String rdbLastSaveStatus;

    @ApiModelProperty(value = "Key数量")
    private String keys;

    @ApiModelProperty(value = "网络入口/出口")
    private String instantaneousInputKbps;
    private String instantaneousOutputKbps;
}
