package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 字典数据表
 */
@Data
@TableName("sys_dict_data")
@ApiModel(value = "字典数据")
public class SysDictData {


    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "字典数据id")
    private Long id;

    @ApiModelProperty(value = "字典类型id")
    private Long dictId;

    @ApiModelProperty(value = "字典标签")
    private String label;

    @ApiModelProperty(value = "字典键值")
    private String value;

    @ApiModelProperty(value = "回显样式")
    private String style;

    @ApiModelProperty(value = "是否默认（1是 0否）")
    private Integer isDefault;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "状态")
    private Integer status;
}
