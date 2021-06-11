package com.gongbo.excel.imports.core;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.listener.ReadListener;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 导入数据读取
 */
public class ImportReadListener implements ReadListener<Object> {

    /**
     * 导入数据
     */
    private Collection<Object> list;

    /**
     * 导入完成回调
     */
    private BiConsumer<Collection<Object>, AnalysisContext> completedConsumer;

    private BiConsumer<Exception, AnalysisContext> exceptionConsumer;

    public ImportReadListener(Collection<Object> list,
                              BiConsumer<Collection<Object>, AnalysisContext> completedConsumer,
                              BiConsumer<Exception, AnalysisContext> exceptionConsumer) {
        this.list = list;
        this.completedConsumer = completedConsumer;
        this.exceptionConsumer = exceptionConsumer;
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        if (exceptionConsumer != null) {
            exceptionConsumer.accept(exception, context);
        }
    }

    @Override
    public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {

    }

    @Override
    public void invoke(Object data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (completedConsumer != null) {
            completedConsumer.accept(list, context);
        }
    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return true;
    }
}
