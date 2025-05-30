package com.mojian.vo.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mojian.utils.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "聊天消息")
public class ChatSendMsgVo {

    @ApiModelProperty(value = "消息id")
    private Long id;

    @ApiModelProperty(value = "消息类型，可选值为'text'、'image'，默认'text'")
    private String type;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "是否自己发送")
    private Long userId;

    @ApiModelProperty(value = "发送人名称")
    private String name;

    @ApiModelProperty(value = "发送人头像")
    private String avatar;

    @ApiModelProperty(value = "归属地")
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
    @JsonFormat(pattern = DateUtil.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime time;

    @ApiModelProperty(value = "回复的消息id")
    private String replyId;

    @ApiModelProperty(value = "回复的用户id")
    private String replyUserId;

    @ApiModelProperty(value = "回复的消息内容")
    private String replyContent;

    @ApiModelProperty(value = "回复用户昵称")
    private String replyUserName;

}
