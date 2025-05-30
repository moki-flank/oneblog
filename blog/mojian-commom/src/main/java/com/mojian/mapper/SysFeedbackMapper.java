package com.mojian.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mojian.dto.feedback.SysFeedbackQueryDto;
import com.mojian.entity.SysFeedback;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mojian.vo.feedback.SysFeedbackVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 反馈表 Mapper接口
 */
@Mapper
public interface SysFeedbackMapper extends BaseMapper<SysFeedback> {
    /**
     * 分页查询
     * @param page
     * @param sysFeedback
     * @return
     */
    IPage<SysFeedbackVo> page(@Param("page") Page<Object> page, @Param("sysFeedback") SysFeedbackQueryDto sysFeedback);
}
