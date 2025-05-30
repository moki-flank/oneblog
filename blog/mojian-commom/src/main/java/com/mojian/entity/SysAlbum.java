package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
@TableName("sys_album")
@ApiModel(value = "相册对象")
public class SysAlbum implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "封面图")
    private String cover;

    @ApiModelProperty(value = "相册名")
    private String name;

    @ApiModelProperty(value = "相册描述")
    private String description;

    @ApiModelProperty(value = "是否加密 0：否 1：是")
    private Integer isLock;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD, timezone = "GMT+8")
    private LocalDateTime createTime;


    @TableField(exist = false)
    private Integer photoNum;
}
