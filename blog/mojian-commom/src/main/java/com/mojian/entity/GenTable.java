package com.mojian.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
@ApiModel(value = "代码生成业务对象 gen_table")
public class GenTable {

    @ApiModelProperty(value = "表ID")
    private Long tableId;

    @ApiModelProperty(value = "表名称")
    private String tableName;

    @ApiModelProperty(value = "表描述")
    private String tableComment;

    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS, timezone = "GMT+8")
    private Date createTime;


    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS, timezone = "GMT+8")
    private Date updateTime;


    // 主键列信息
    private GenTableColumn pkColumn;

    // 分页参数
    private Integer offset;
    private Integer pageSize;

    @ApiModelProperty(value = "实体类名称")
    private String className;

    @ApiModelProperty(value = "生成包路径")
    private String packageName;

    @ApiModelProperty(value = "生成模块名")
    private String moduleName;

    @ApiModelProperty(value = "生成业务名")
    private String businessName;
}
