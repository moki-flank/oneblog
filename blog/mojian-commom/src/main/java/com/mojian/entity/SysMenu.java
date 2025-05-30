package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.enums.MenuTypeEnum;
import com.mojian.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
@ApiModel(value = "菜单信息")
public class SysMenu implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "父菜单ID，一级菜单为0")
    private Integer parentId;

    @ApiModelProperty(value = "路由地址")
    private String path;

    @ApiModelProperty(value = "组件路径")
    private String component;

    @ApiModelProperty(value = "菜单名称")
    private String title;

    @ApiModelProperty(value = "显示顺序")
    private Integer sort;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "菜单类型（CATALOG目录 MENU菜单 BUTTON按钮）")
    private MenuTypeEnum type;

    @ApiModelProperty(value = "重定向地址")
    private String redirect;

    @ApiModelProperty(value = "路由的name")
    private String name;

    @ApiModelProperty(value = "是否隐藏路由，当设置 true 的时候该路由在侧边栏隐藏")
    private Integer hidden;

    @ApiModelProperty(value = "是否外链 0:否  1:是")
    private Integer isExternal;

    @ApiModelProperty(value = "权限标识")
    private String perm;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS, timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS, timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<SysMenu> children;
}
