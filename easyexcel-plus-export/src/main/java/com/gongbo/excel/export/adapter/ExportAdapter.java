package com.gongbo.excel.export.adapter;

import com.gongbo.excel.common.adapter.Adapter;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.entity.ExportFieldInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface ExportAdapter extends Adapter {

    /**
     * 普通导出
     *
     * @param exportContext
     * @param data
     * @param outputStream
     * @throws IOException
     */
    void export(ExportContext exportContext, List<?> data, OutputStream outputStream) throws IOException;

    /**
     * 模板导出
     *
     * @param exportContext
     * @param templateInputStream
     * @param data
     * @param outputStream
     */
    void export(ExportContext exportContext, InputStream templateInputStream, List<?> data, OutputStream outputStream);

    /**
     * 查找导出的字段信息列表
     *
     * @param clazz
     * @return
     */
    List<ExportFieldInfo> findExportFieldInfos(Class<?> clazz);
}
