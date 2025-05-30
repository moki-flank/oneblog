package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
@TableName("sys_file_oss")
@ApiModel(value = "存储平台对象")
public class SysFileOss implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "访问域名")
    private String domain;

    @ApiModelProperty(value = "access-key")
    private String accessKey;

    @ApiModelProperty(value = "secret-key")
    private String secretKey;

    @ApiModelProperty(value = "空间名")
    private String bucket;

    @ApiModelProperty(value = "存储基础路径")
    private String basePath;

    @ApiModelProperty(value = "存储类型")
    private String platform;

    @ApiModelProperty(value = "是否启用存储")
    private Integer isEnable;

    @ApiModelProperty(value = "本地存储路径")
    private String storagePath;

    @ApiModelProperty(value = "本地启用访问")
    private Integer enableAccess;

    @ApiModelProperty(value = "本地访问路径")
    private String pathPatterns;

    @ApiModelProperty(value = "仓库所在地域")
    private String region;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
