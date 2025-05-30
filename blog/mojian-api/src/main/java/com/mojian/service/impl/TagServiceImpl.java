package com.mojian.service.impl;

import com.mojian.service.TagService;
import com.mojian.vo.tag.TagListVo;
import com.mojian.mapper.SysTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final SysTagMapper sysTagMapper;

    @Override
    public List<TagListVo> getTagsApi() {
        return sysTagMapper.getTagsApi();
    }
}
