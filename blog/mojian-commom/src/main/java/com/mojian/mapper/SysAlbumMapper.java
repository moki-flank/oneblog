package com.mojian.mapper;

import com.mojian.entity.SysAlbum;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 相册 Mapper接口
 */
@Mapper
public interface SysAlbumMapper extends BaseMapper<SysAlbum> {

    List<SysAlbum> getAlbumList();

}
