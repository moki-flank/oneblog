package com.mojian.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: quequnlong
 * @date: 2024/12/28
 * @description: 邮箱注册DTO
 */
@Data
public class EmailRegisterDto {

    @ApiModelProperty(value = "邮箱")
    @NotNull(message = "邮箱不能为空")
    private String email;

    @ApiModelProperty(value = "验证码")
    @NotNull(message = "验证码不能为空")
    private String code;

    @ApiModelProperty(value = "密码")
    @NotNull(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "昵称")
    @NotNull(message = "昵称不能为空")
    private String nickname;
}
