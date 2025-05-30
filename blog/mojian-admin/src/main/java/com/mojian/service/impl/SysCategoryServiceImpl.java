package com.mojian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mojian.common.ResultCode;
import com.mojian.entity.SysCategory;
import com.mojian.exception.ServiceException;
import com.mojian.mapper.SysCategoryMapper;
import com.mojian.service.SysCategoryService;
import com.mojian.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类表 服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysCategoryServiceImpl extends ServiceImpl<SysCategoryMapper, SysCategory> implements SysCategoryService {

    /**
     * 查询分类表分页列表
     */
    @Override
    public IPage<SysCategory> selectPage(SysCategory sysCategory) {
        LambdaQueryWrapper<SysCategory> wrapper = new LambdaQueryWrapper<SysCategory>()
                .like(StringUtils.isNotBlank(sysCategory.getName()), SysCategory::getName, sysCategory.getName());
        return page(PageUtil.getPage(), wrapper);
    }

    /**
     * 查询分类表列表
     */
    @Override
    public List<SysCategory> selectList(SysCategory sysCategory) {
        return list(null);
    }

    /**
     * 新增分类表
     */
    @Override
    public boolean insert(SysCategory sysCategory) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<SysCategory>()
                .eq(SysCategory::getName, sysCategory.getName()));
        if (count > 0) {
            throw new ServiceException(ResultCode.CATEGORY_IS_EXIST.desc);
        }
        return save(sysCategory);
    }

    /**
     * 修改分类表
     */
    @Override
    public boolean update(SysCategory sysCategory) {
        SysCategory sysCategory1 = baseMapper.selectOne(new LambdaQueryWrapper<SysCategory>().eq(SysCategory::getName, sysCategory.getName()));
        if (sysCategory1 != null && !sysCategory1.getId().equals(sysCategory.getId())) {
            throw new ServiceException(ResultCode.CATEGORY_IS_EXIST.desc);
        }
        return updateById(sysCategory);
    }

    /**
     * 批量删除分类表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(List<Integer> ids) {
        return removeByIds(ids);
    }
}
