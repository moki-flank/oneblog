package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_job")
@ApiModel(value = "定时任务信息")
public class SysJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "job_id", type = IdType.AUTO)
    private Long jobId;

    @ApiModelProperty(value = "任务名称")
    private String jobName;

    @ApiModelProperty(value = "任务组名")
    private String jobGroup;

    @ApiModelProperty(value = "调用目标字符串")
    private String invokeTarget;

    @ApiModelProperty(value = "cron执行表达式")
    private String cronExpression;

    @ApiModelProperty(value = "cron计划策略")
    private String misfirePolicy;

    @ApiModelProperty(value = "是否并发执行（0允许 1禁止）")
    private String concurrent;

    @ApiModelProperty(value = "任务状态")
    private String status;

    @ApiModelProperty(value = "创建")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS,timezone="GMT+8")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS,timezone="GMT+8")
    private Date updateTime;

    private String remark;

    @TableField(exist = false)
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS,timezone="GMT+8")
    private Date nextValidTime;
}
