package com.mojian.service;

import com.mojian.entity.SysAlbum;
import com.mojian.entity.SysPhoto;

import java.util.List;

/**
 * @author: quequnlong
 * @date: 2025/2/7
 * @description:
 */
public interface AlbumService {

    /**
     * 获取相册列表
     * @return
     */
    List<SysAlbum> getAlbumList();

    /**
     * 获取照片列表
     * @param albumId
     * @return
     */
    List<SysPhoto> getPhotos(Long albumId);

    /**
     * 验证相册密码
     * @param id
     * @param password
     * @return
     */
    Boolean verify(Long id, String password);

    /**
     * 获取相册详情
     * @param id
     * @return
     */
    SysAlbum detail(Long id);
}
