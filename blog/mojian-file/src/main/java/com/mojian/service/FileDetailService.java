package com.mojian.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mojian.entity.FileDetail;
import com.mojian.entity.SysFileOss;
import org.dromara.x.file.storage.core.recorder.FileRecorder;

import java.util.List;

/**
 * @author: quequnlong
 * @date: 2025/2/13
 * @description:
 */
public interface FileDetailService extends FileRecorder,IService<FileDetail> {

    /**
     * 查询文件记录表分页列表
     */
    IPage<FileDetail> selectPage(FileDetail fileDetail);

    /**
     * 删除文件
     * @param url
     * @return
     */
    boolean delete(String url);

    /**
     * 获取存储平台配置
     * @return
     */
    List<SysFileOss> getOssConfig();

    /**
     * 添加存储平台配置
     * @param sysFileOss
     */
    void addOss(SysFileOss sysFileOss);

    /**
     * 修改存储平台配置
     * @param sysFileOss
     */
    void updateOss(SysFileOss sysFileOss);

}
