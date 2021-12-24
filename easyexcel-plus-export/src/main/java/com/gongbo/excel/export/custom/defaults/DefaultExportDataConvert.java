package com.gongbo.excel.export.custom.defaults;

import com.gongbo.excel.common.result.ResultHandler;
import com.gongbo.excel.export.custom.ExportDataConvert;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.utils.ExportUtils;

import java.util.List;

public class DefaultExportDataConvert implements ExportDataConvert {

    @Override
    public List<?> convert(ExportContext exportContext, Object responseEntity) {
        ResultHandler resultHandler = exportContext.getResultHandler();

        if (resultHandler.check(responseEntity)) {
            Object data = resultHandler.getData(responseEntity);
            if (data instanceof ExportDataConvert) {
                return ((ExportDataConvert) data).convert(exportContext, responseEntity);
            }

            return ExportUtils.objectToList(data);
        }

        throw new IllegalArgumentException("");
    }
}