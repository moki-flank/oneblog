package com.mojian.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mojian.entity.SysCategory;

import java.util.List;

/**
 * 分类表 服务接口
 */
public interface SysCategoryService extends IService<SysCategory> {
    /**
     * 查询分类表分页列表
     */
    IPage<SysCategory> selectPage(SysCategory sysCategory);

    /**
     * 查询分类表列表
     */
    List<SysCategory> selectList(SysCategory sysCategory);

    /**
     * 新增分类表
     */
    boolean insert(SysCategory sysCategory);

    /**
     * 修改分类表
     */
    boolean update(SysCategory sysCategory);

    /**
     * 批量删除分类表
     */
    boolean deleteByIds(List<Integer> ids);

}