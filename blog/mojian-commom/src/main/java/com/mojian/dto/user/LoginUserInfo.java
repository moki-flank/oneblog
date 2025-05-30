package com.mojian.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "登录用户信息")
public class LoginUserInfo implements Serializable {

    @ApiModelProperty(value = "用户ID")
    private Integer id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "权限列表")
    private List<String> permissions;

    @ApiModelProperty(value = "角色信息")
    private List<String> roles;
}
