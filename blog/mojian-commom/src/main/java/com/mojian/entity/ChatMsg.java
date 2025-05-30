package com.mojian.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("chat_msg")
@ApiModel(value = "聊天消息对象")
public class ChatMsg implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "消息ID")
    private Long id;

    @ApiModelProperty(value = "聊天ID")
    private Long chatId;

    @ApiModelProperty(value = "发送人ID")
    private Long senderId;

    @ApiModelProperty(value = "消息类型，可选值为'text'、'image'，默认'text'")
    private String type;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "ip归属地")
    private String location;

    @ApiModelProperty(value = "是否撤回，默认false")
    private Boolean isRecalled;

    @ApiModelProperty(value = "消息是文件时，存的文件名")
    private String fileName;

    @ApiModelProperty(value = "文件大小")
    private String fileSize;

    @ApiModelProperty(value = "语音时长")
    private Integer duration;

    @ApiModelProperty(value = "发送时间")
    private LocalDateTime createTime;


    @ApiModelProperty(value = "回复的消息id")
    private String replyId;

    @ApiModelProperty(value = "回复的用户id")
    private String replyUserId;

    @ApiModelProperty(value = "回复的消息内容")
    private String replyContent;

}
