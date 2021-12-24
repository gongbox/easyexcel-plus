package com.gongbo.excel.adapter.easyexcel.converter;


import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.DateUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter implements Converter<LocalDate> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDate convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return LocalDate.parse(cellData.getStringValue(), DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_10));
        } else {
            return LocalDate.parse(cellData.getStringValue(),
                    DateTimeFormatter.ofPattern(contentProperty.getDateTimeFormatProperty().getFormat()));
        }
    }

    @Override
    public CellData<?> convertToExcelData(LocalDate value,
                                          ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return new CellData<>(value.format(DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_10)));
        } else {
            return new CellData<>(value.format(DateTimeFormatter.ofPattern(contentProperty.getDateTimeFormatProperty().getFormat())));
        }
    }
}
