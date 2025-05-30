package com.mojian.vo.cache;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "缓存内存信息")
public class CacheMemoryVo {

    @ApiModelProperty(value = "已用内存")
    private Long used;

    @ApiModelProperty(value = "总内存")
    private Long total;
}
