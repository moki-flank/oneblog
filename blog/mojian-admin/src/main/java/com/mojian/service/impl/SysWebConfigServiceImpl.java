package com.mojian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mojian.entity.SysWebConfig;
import com.mojian.mapper.SysWebConfigMapper;
import com.mojian.service.SysWebConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysWebConfigServiceImpl extends ServiceImpl<SysWebConfigMapper, SysWebConfig> implements SysWebConfigService {
}
