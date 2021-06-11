package com.gongbo.excel.export.core.handler;


import com.gongbo.excel.export.entity.ExportContext;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 导出执行
 */
public interface WriteHandler {

    /**
     * @param exportContext
     * @param data
     * @param outputStream
     * @throws IOException
     */
    void write(ExportContext exportContext, List<?> data, OutputStream outputStream) throws IOException;
}
