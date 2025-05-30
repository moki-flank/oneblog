package com.mojian.mapper;

import com.mojian.entity.FilePartDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件分片信息表，仅在手动分片上传时使用 Mapper接口
 */
@Mapper
public interface FilePartDetailMapper extends BaseMapper<FilePartDetail> {
}
