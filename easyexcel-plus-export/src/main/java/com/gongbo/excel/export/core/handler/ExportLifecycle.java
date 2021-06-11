package com.gongbo.excel.export.core.handler;


import com.gongbo.excel.export.entity.ExportContext;

import java.util.List;

/**
 * 导出后执行
 */
public interface ExportLifecycle {

    void afterPrepared(ExportContext exportContext);

    void afterDataConverted(ExportContext exportContext, List<?> data);

    /**
     * @param exportContext
     */
    void afterExport(ExportContext exportContext);

}
