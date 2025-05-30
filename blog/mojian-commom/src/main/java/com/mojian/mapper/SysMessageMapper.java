package com.mojian.mapper;

import com.mojian.entity.SysMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 留言 Mapper接口
 */
@Mapper
public interface SysMessageMapper extends BaseMapper<SysMessage> {
}