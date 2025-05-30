package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

@Data
@TableName("file_detail")
@ApiModel(value = "文件记录表对象")
public class FileDetail implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "文件id")
    private String id;

    @ApiModelProperty(value = "文件访问地址")
    private String url;

    @ApiModelProperty(value = "文件大小，单位字节")
    private Long size;

    @ApiModelProperty(value = "文件名称")
    private String filename;

    @ApiModelProperty(value = "原始文件名")
    private String originalFilename;

    @ApiModelProperty(value = "基础存储路径")
    private String basePath;

    @ApiModelProperty(value = "存储路径")
    private String path;

    @ApiModelProperty(value = "文件扩展名")
    private String ext;

    @ApiModelProperty(value = "MIME类型")
    private String contentType;

    @ApiModelProperty(value = "存储平台")
    private String platform;

    @ApiModelProperty(value = "缩略图访问路径")
    private String thUrl;

    @ApiModelProperty(value = "缩略图名称")
    private String thFilename;

    @ApiModelProperty(value = "缩略图大小，单位字节")
    private Long thSize;

    @ApiModelProperty(value = "缩略图MIME类型")
    private String thContentType;

    @ApiModelProperty(value = "文件所属对象id")
    private String objectId;

    @ApiModelProperty(value = "文件所属对象类型，例如用户头像，评价图片")
    private String objectType;

    @ApiModelProperty(value = "文件元数据")
    private String metadata;

    @ApiModelProperty(value = "文件用户元数据")
    private String userMetadata;

    @ApiModelProperty(value = "缩略图元数据")
    private String thMetadata;

    @ApiModelProperty(value = "缩略图用户元数据")
    private String thUserMetadata;

    @ApiModelProperty(value = "附加属性")
    private String attr;

    @ApiModelProperty(value = "文件ACL")
    private String fileAcl;

    @ApiModelProperty(value = "缩略图文件ACL")
    private String thFileAcl;

    @ApiModelProperty(value = "哈希信息")
    private String hashInfo;

    @ApiModelProperty(value = "上传ID，仅在手动分片上传时使用")
    private String uploadId;

    @ApiModelProperty(value = "上传状态，仅在手动分片上传时使用，1：初始化完成，2：上传完成")
    private Integer uploadStatus;

    @ApiModelProperty(value = "图片来源")
    private String source;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS, timezone = "GMT+8")
    private LocalDateTime createTime;
}
