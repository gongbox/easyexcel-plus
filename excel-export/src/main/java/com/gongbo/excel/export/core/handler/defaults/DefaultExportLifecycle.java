package com.gongbo.excel.export.core.handler.defaults;

import com.gongbo.excel.export.core.handler.ExportLifecycle;
import com.gongbo.excel.export.entity.ExportContext;

import java.util.List;

public class DefaultExportLifecycle implements ExportLifecycle {
    @Override
    public void afterPrepared(ExportContext exportContext) {

    }

    @Override
    public void afterDataConverted(ExportContext exportContext, List<?> data) {

    }

    @Override
    public void afterExport(ExportContext exportContext) {
    }
}