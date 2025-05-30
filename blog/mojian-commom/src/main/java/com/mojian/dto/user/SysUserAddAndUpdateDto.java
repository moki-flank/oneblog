package com.mojian.dto.user;

import com.mojian.entity.SysUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "新增用户参数")
public class SysUserAddAndUpdateDto {

    @ApiModelProperty(value = "用户信息")
    private SysUser user;

    @ApiModelProperty(value = "角色ID列表")
    private List<Integer> roleIds;
}
