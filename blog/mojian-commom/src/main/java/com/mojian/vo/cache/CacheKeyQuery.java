package com.mojian.vo.cache;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "缓存键查询参数")
public class CacheKeyQuery {

    @ApiModelProperty(value = "键名关键字")
    private String key;

    @ApiModelProperty(value = "页码")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize = 10;
}
