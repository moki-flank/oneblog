package com.mojian.service;

import com.mojian.entity.SysNotice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 公告 服务接口
 */
public interface SysNoticeService extends IService<SysNotice> {
    /**
     * 查询公告分页列表
     */
    IPage<SysNotice> selectPage(SysNotice sysNotice);

    /**
     * 查询公告列表
     */
    List<SysNotice> selectList(SysNotice sysNotice);

    /**
     * 新增公告
     */
    boolean insert(SysNotice sysNotice);

    /**
     * 修改公告
     */
    boolean update(SysNotice sysNotice);

    /**
     * 批量删除公告
     */
    boolean deleteByIds(List<Long> ids);
}
