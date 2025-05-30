package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("file_part_detail")
@ApiModel(value = "文件分片信息表，仅在手动分片上传时使用对象")
public class FilePartDetail implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "分片id")
    private String id;

    @ApiModelProperty(value = "存储平台")
    private String platform;

    @ApiModelProperty(value = "上传ID，仅在手动分片上传时使用")
    private String uploadId;

    @ApiModelProperty(value = "分片 ETag")
    private String eTag;

    @ApiModelProperty(value = "分片号。每一个上传的分片都有一个分片号，一般情况下取值范围是1~10000")
    private Integer partNumber;

    @ApiModelProperty(value = "文件大小，单位字节")
    private Long partSize;

    @ApiModelProperty(value = "哈希信息")
    private String hashInfo;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
