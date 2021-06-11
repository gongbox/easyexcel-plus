package com.gongbo.excel.export.core.handler.defaults;

import com.gongbo.excel.export.core.handler.WriteHandler;
import com.gongbo.excel.export.core.provider.easyexcel.EasyExcelProvider;
import com.gongbo.excel.export.entity.ExportContext;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class DefaultWriteHandler implements WriteHandler {

    @Override
    public void write(ExportContext exportContext, List<?> data, OutputStream outputStream) throws IOException {
        EasyExcelProvider.getInstance().export(exportContext, data, outputStream);
    }

}