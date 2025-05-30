package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 友情链接
 */
@Data
@TableName("sys_friend")
@ApiModel(value = "友情链接对象 gen_table")
public class SysFriend implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "网站名称")
    private String name;

    @ApiModelProperty(value = "网站地址")
    private String url;

    @ApiModelProperty(value = "网站头像地址")
    private String avatar;

    @ApiModelProperty(value = "网站描述")
    private String info;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "下架原因")
    private String reason;

    @ApiModelProperty(value = "ENUM-状态:0,下架;1,申请;2:上架")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime updateTime;
}
