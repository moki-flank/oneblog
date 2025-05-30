package com.mojian.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mojian.entity.SysPhoto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: quequnlong
 * @date: 2025/2/7
 * @description:
 */
@Mapper
public interface SysPhotoMapper extends BaseMapper<SysPhoto> {

    void move(@Param("ids") List<Long> ids, @Param("albumId") Long albumId);
}
