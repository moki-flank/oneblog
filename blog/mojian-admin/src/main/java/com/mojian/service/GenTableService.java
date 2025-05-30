package com.mojian.service;

import com.mojian.entity.GenTable;

import java.util.Map;
import java.util.zip.ZipOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.HashMap;

public interface GenTableService {

    Map<String, Object> selectGenTableList(GenTable genTable);

    Map<String, String> previewCode(Long tableId);

    int deleteGenTableByIds(Long[] tableIds);

    String syncDb(String tableName);

    Map<String, Object> selectDbTableList(GenTable genTable);

    void importGenTable(String[] tableNames);

    byte[] downloadCode(String[] tableNames) throws IOException;

}
