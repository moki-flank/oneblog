package com.mojian.vo.cache;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "缓存键信息")
public class CacheKeyVo {

    @ApiModelProperty(value = "键名")
    private String key;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "大小")
    private Long size;

    @ApiModelProperty(value = "过期时间")
    private Long ttl;
}
