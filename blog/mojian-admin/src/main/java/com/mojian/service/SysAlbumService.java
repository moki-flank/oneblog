package com.mojian.service;

import com.mojian.entity.SysAlbum;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 相册 服务接口
 */
public interface SysAlbumService extends IService<SysAlbum> {
    /**
     * 查询相册分页列表
     */
    IPage<SysAlbum> selectPage(SysAlbum sysAlbum);

    /**
     * 查询相册列表
     */
    List<SysAlbum> selectList(SysAlbum sysAlbum);

    /**
     * 新增相册
     */
    boolean insert(SysAlbum sysAlbum);

    /**
     * 修改相册
     */
    boolean update(SysAlbum sysAlbum);

    /**
     * 批量删除相册
     */
    boolean deleteByIds(List<Long> ids);
}
