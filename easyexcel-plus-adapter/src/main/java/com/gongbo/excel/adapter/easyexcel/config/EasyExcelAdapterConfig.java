package com.gongbo.excel.adapter.easyexcel.config;

import com.gongbo.excel.adapter.easyexcel.EasyExcelAdapter;
import com.gongbo.excel.export.adapter.ExportAdapters;
import com.gongbo.excel.imports.adapter.ImportAdapters;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EasyExcelAdapterConfig {

    static {
        EasyExcelAdapter adapter = new EasyExcelAdapter();
        ExportAdapters.addAdapter(adapter);
        ImportAdapters.addAdapter(adapter);
    }
}
