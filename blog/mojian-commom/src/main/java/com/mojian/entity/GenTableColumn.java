package com.mojian.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "代码生成业务字段信息")
public class GenTableColumn {

    @ApiModelProperty(value = "字段主键")
    private Long columnId;

    @ApiModelProperty(value = "表Id")
    private Long tableId;

    @ApiModelProperty(value = "字段名称")
    private String columnName;

    @ApiModelProperty(value = "字段描述")
    private String columnComment;

    @ApiModelProperty(value = "物理类型")
    private String columnType;

    @ApiModelProperty(value = "Java属性类型")
    private String javaType;

    @ApiModelProperty(value = "Java属性名")
    private String javaField;

    @ApiModelProperty(value = "是否主键（1是）")
    private String isPk;

    @ApiModelProperty(value = "是否必填（1是）")
    private String isRequired;

    @ApiModelProperty(value = "是否为插入字段（1是）")
    private String isInsert;

    @ApiModelProperty(value = "是否编辑字段（1是）")
    private String isEdit;

    @ApiModelProperty(value = "是否列表字段（1是）")
    private String isList;

    @ApiModelProperty(value = "是否查询字段（1是）")
    private String isQuery;

    @ApiModelProperty(value = "查询方式（EQ等于、NE不等于、GT大于、LT小于、LIKE模糊、BETWEEN范围）")
    private String queryType;

    @ApiModelProperty(value = "显示类型（input文本框、textarea文本域、select下拉框、checkbox复选框、radio单选框、datetime日期控件、img图片上传控件、file文件上传控件、editor富文本控件）")
    private String htmlType;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    private String columnKey;
}
