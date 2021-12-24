package com.gongbo.excel.imports.adapter;


import com.gongbo.excel.common.adapter.Adapter;
import com.gongbo.excel.imports.entity.ImportContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ImportAdapter extends Adapter {

    /**
     * 读取数据
     *
     * @param importContext
     * @param inputStream
     * @return
     * @throws IOException
     */
    Collection<?> read(ImportContext importContext, InputStream inputStream) throws IOException, ExecutionException, InterruptedException, TimeoutException;

    /**
     * 响应模板
     *
     * @param importContext
     * @param outputStream
     */
    void responseTemplate(ImportContext importContext, OutputStream outputStream) throws IOException;
}
