package com.gongbo.excel.export.custom.defaults;

import com.gongbo.excel.export.core.resulthandler.ResultHandler;
import com.gongbo.excel.export.custom.ExportDataConvert;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.utils.ExportUtils;

import java.util.List;

public class DefaultExportDataConvert implements ExportDataConvert {

    @Override
    public List<?> convert(ExportContext exportContext, Object result) {
        ResultHandler resultHandler = exportContext.getResultHandler();

        Object data = resultHandler.getResultData(result);
        if (data instanceof ExportDataConvert) {
            return ((ExportDataConvert) data).convert(exportContext, result);
        }

        return ExportUtils.objectToList(data);
    }
}