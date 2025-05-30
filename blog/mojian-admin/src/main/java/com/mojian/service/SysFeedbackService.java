package com.mojian.service;

import com.mojian.dto.feedback.SysFeedbackQueryDto;
import com.mojian.entity.SysFeedback;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mojian.vo.feedback.SysFeedbackVo;

import java.util.List;

/**
 * 反馈表 服务接口
 */
public interface SysFeedbackService extends IService<SysFeedback> {
    /**
     * 查询反馈表分页列表
     */
    IPage<SysFeedbackVo> selectPage(SysFeedbackQueryDto feedbackQueryDto);

    /**
     * 新增反馈表
     */
    boolean insert(SysFeedback sysFeedback);

    /**
     * 修改反馈表
     */
    boolean update(SysFeedback sysFeedback);
}
