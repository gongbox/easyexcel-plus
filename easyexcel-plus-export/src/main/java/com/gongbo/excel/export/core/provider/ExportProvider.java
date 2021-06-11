package com.gongbo.excel.export.core.provider;


import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.entity.ExportFieldInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;

public interface ExportProvider {

    /**
     * 获取字段信息
     *
     * @param field
     * @return
     */
    ExportFieldInfo findExportFieldInfo(Field field);

    void export(ExportContext exportContext, List<?> data, OutputStream outputStream) throws IOException;

}
