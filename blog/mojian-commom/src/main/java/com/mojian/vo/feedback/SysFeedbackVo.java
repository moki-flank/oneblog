package com.mojian.vo.feedback;

import com.mojian.entity.SysFeedback;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: quequnlong
 * @date: 2025/1/12
 * @description:
 */
@Data
@ApiModel(value = "反馈对象vo")
public class SysFeedbackVo extends SysFeedback {

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "头像")
    private String avatar;
}
