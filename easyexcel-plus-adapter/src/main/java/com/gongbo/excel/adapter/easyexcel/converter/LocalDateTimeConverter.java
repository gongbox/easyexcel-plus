package com.gongbo.excel.adapter.easyexcel.converter;


import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.DateUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter implements Converter<LocalDateTime> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return LocalDateTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDateTime convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return LocalDateTime.parse(cellData.getStringValue(), DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_19));
        } else {
            return LocalDateTime.parse(cellData.getStringValue(),
                    DateTimeFormatter.ofPattern(contentProperty.getDateTimeFormatProperty().getFormat()));
        }
    }

    @Override
    public CellData<?> convertToExcelData(LocalDateTime value,
                                          ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return new CellData<>(value.format(DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_19)));
        } else {
            return new CellData<>(value.format(DateTimeFormatter.ofPattern(contentProperty.getDateTimeFormatProperty().getFormat())));
        }
    }
}
