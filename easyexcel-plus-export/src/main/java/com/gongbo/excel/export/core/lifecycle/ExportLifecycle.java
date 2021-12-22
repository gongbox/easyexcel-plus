package com.gongbo.excel.export.core.lifecycle;

import com.gongbo.excel.export.adapter.ExportAdapter;
import com.gongbo.excel.export.config.ExportProperties;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.param.ExportParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public interface ExportLifecycle {
    /**
     * 当前请求是否是导出请求
     *
     * @return
     */
    boolean isExportRequest(ExportProperties exportProperties, HttpServletRequest request);

    /**
     * 导出参数提取
     *
     * @return
     */
    ExportParam prepareParam(ExportProperties exportProperties, HttpServletRequest request);

    /**
     * 导出上下文
     *
     * @param exportParam
     * @param targetMethod
     * @return
     */
    ExportContext prepareContext(ExportProperties exportProperties, ExportParam exportParam, Method targetMethod);

    /**
     * ExportAdapter选择
     *
     * @param exportContext
     * @param adapters
     * @return
     */
    ExportAdapter selectAdapter(ExportContext exportContext, Collection<ExportAdapter> adapters);

    /**
     * 列出导出字段信息列表
     */
    void prepareExportFieldInfos(ExportContext exportContext, ExportAdapter exportAdapter);

    /**
     * 准备导出数据
     *
     * @param exportContext
     * @param result
     * @return
     */
    List<?> prepareData(ExportContext exportContext, Object result);


    /**
     * 准备输出流
     *
     * @param exportContext
     */
    OutputStream prepareOutputStream(ExportContext exportContext) throws IOException;

    /**
     * 导出
     *
     * @param exportContext
     * @param data
     * @param outputStream
     */
    void export(ExportContext exportContext, List<?> data, ExportAdapter adapter, OutputStream outputStream) throws IOException;

    /**
     * 重置
     *
     * @param exportContext
     */
    void reset(ExportContext exportContext);
}
