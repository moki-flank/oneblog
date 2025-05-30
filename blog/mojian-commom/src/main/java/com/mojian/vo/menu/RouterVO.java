package com.mojian.vo.menu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "路由对象")
public class RouterVO {

    @ApiModelProperty(value = "菜单ID")
    private Integer id;

    @ApiModelProperty(value = "组件")
    private String component;

    @ApiModelProperty(value = "路由地址")
    private String path;

    @ApiModelProperty(value = "路由名称")
    private String name;

    @ApiModelProperty(value = "跳转地址")
    private String redirect;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "菜单属性")
    private MetaVO meta;

    private List<RouterVO> children;

    @Data
public static class MetaVO{

        @ApiModelProperty(value = "菜单标题")
        private String title;

        @ApiModelProperty(value = "菜单图标")
        private String icon;

        @ApiModelProperty(value = "是否隐藏")
        private Boolean hidden;

        @ApiModelProperty(value = "是否外链")
        private Boolean isExternal;

        public MetaVO(String title, String icon, Integer hidden, Integer isExternal) {
            this.title = title;
            this.icon = icon;
            this.hidden = hidden == 1;
            this.isExternal = isExternal == 1;
        }
    }
}
