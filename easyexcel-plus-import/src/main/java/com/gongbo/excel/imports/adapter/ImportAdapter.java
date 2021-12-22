package com.gongbo.excel.imports.adapter;


import com.gongbo.excel.imports.entity.ImportContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ImportAdapter {
    /**
     * 标签
     *
     * @return
     */
    String name();

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
     * @param response
     */
    void responseTemplate(ImportContext importContext, HttpServletResponse response) throws IOException;
}
