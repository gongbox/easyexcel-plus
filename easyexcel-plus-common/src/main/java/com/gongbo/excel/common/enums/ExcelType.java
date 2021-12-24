package com.gongbo.excel.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExcelType {
    XLS(".xls"),
    XLSX(".xlsx");

    private final String value;
}