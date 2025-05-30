package com.mojian.service.impl;

import org.springframework.stereotype.Service;
import com.mojian.mapper.SysOperateLogMapper;
import com.mojian.entity.SysOperateLog;
import com.mojian.service.SysOperateLogService;
import com.mojian.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;

/**
 *  服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysOperateLogServiceImpl extends ServiceImpl<SysOperateLogMapper, SysOperateLog> implements SysOperateLogService {

    /**
     * 查询分页列表
     */
    @Override
    public IPage<SysOperateLog> listSysOperateLog(SysOperateLog sysOperateLog) {
        LambdaQueryWrapper<SysOperateLog> wrapper = new LambdaQueryWrapper<>();
        return page(PageUtil.getPage(), wrapper);
    }
}
