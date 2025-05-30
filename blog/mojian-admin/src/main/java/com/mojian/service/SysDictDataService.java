package com.mojian.service;

import com.mojian.entity.SysDictData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

/**
 * 字典数据表 服务接口
 */
public interface SysDictDataService extends IService<SysDictData> {
    /**
     * 查询字典数据分页列表
     */
    IPage<SysDictData> listDictData(Long dictId);

    /**
     * 新增字典数据
     */
    boolean addDictData(SysDictData sysDictData);

    /**
     * 修改字典数据
     */
    boolean updateDictData(SysDictData sysDictData);

    /**
     * 根据字典类型查询字典数据
     * @param dictTypes
     * @return
     */
    Map<String, Map<String, Object>> getDiceDataByDictType(List<String> dictTypes);
}
