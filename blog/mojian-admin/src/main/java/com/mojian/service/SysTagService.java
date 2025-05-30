package com.mojian.service;

import com.mojian.entity.SysTag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 标签表 服务接口
 */
public interface SysTagService extends IService<SysTag> {
    /**
     * 查询标签表分页列表
     */
    IPage<SysTag> selectPage(SysTag sysTag);

    /**
     * 查询标签表列表
     */
    List<SysTag> selectList(SysTag sysTag);

    /**
     * 新增标签表
     */
    boolean insert(SysTag sysTag);

    /**
     * 修改标签表
     */
    boolean update(SysTag sysTag);

    /**
     * 批量删除标签表
     */
    boolean deleteByIds(List<Integer> ids);

}