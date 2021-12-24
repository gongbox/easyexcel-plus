package com.gongbo.excel.export.constants;

import com.gongbo.excel.common.enums.ExcelType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExportExcelType {
    /**
     * 自动，
     * 1：模板导出时，根据模板格式确定
     * 2：非模板导出时，取默认配置导出
     */
    AUTO(null),
    XLS(ExcelType.XLS),
    XLSX(ExcelType.XLSX);

    private final ExcelType excelType;
}