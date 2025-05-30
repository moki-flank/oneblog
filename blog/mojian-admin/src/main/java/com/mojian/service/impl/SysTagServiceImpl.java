package com.mojian.service.impl;

import java.util.List;

import com.mojian.common.ResultCode;
import com.mojian.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.mojian.mapper.SysTagMapper;
import com.mojian.entity.SysTag;
import com.mojian.service.SysTagService;
import com.mojian.utils.PageUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/**
 * 标签表 服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysTagServiceImpl extends ServiceImpl<SysTagMapper, SysTag> implements SysTagService {

    /**
     * 查询标签表分页列表
     */
    @Override
    public IPage<SysTag> selectPage(SysTag sysTag) {
        LambdaQueryWrapper<SysTag> wrapper = new LambdaQueryWrapper<SysTag>()
                .like(StringUtils.isNotBlank(sysTag.getName()), SysTag::getName, sysTag.getName());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询标签表列表
     */
    @Override
    public List<SysTag> selectList(SysTag sysTag) {
        return list(null);
    }

    /**
     * 新增标签表
     */
    @Override
    public boolean insert(SysTag sysTag) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<SysTag>()
                .eq(SysTag::getName, sysTag.getName()));
        if (count > 0) {
            throw new ServiceException(ResultCode.TAG_IS_EXIST.desc);
        }
        return save(sysTag);
    }

    /**
     * 修改标签表
     */
    @Override
    public boolean update(SysTag sysTag) {
        SysTag sysTag1 = baseMapper.selectOne(new LambdaQueryWrapper<SysTag>().eq(SysTag::getName, sysTag.getName()));
        if (sysTag1 != null && !sysTag1.getId().equals(sysTag.getId())) {
            throw new ServiceException(ResultCode.TAG_IS_EXIST.desc);
        }
        return updateById(sysTag);
    }

    /**
     * 批量删除标签表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Integer> ids) {
        return removeByIds(ids);
    }
}
