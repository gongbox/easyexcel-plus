package com.gongbo.excel.export.constants;

import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExcelType {
    /**
     * 自动，
     * 1：模板导出时，根据模板格式确定
     * 2：非模板导出时，取默认配置导出
     */
    AUTO(null),

    /**
     *
     */
    XLS(ExcelTypeEnum.XLS),

    /**
     *
     */
    XLSX(ExcelTypeEnum.XLSX);

    private final ExcelTypeEnum excelTypeEnum;
}