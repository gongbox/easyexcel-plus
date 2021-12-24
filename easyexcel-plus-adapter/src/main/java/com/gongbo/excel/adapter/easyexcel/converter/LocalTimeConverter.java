package com.gongbo.excel.adapter.easyexcel.converter;


import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.gongbo.excel.common.utils.Times;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeConverter implements Converter<LocalTime> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return LocalTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalTime convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return LocalTime.parse(cellData.getStringValue(), DateTimeFormatter.ofPattern(Times.Pattern.DEFAULT_TIME));
        } else {
            return LocalTime.parse(cellData.getStringValue(),
                    DateTimeFormatter.ofPattern(contentProperty.getDateTimeFormatProperty().getFormat()));
        }
    }

    @Override
    public CellData<?> convertToExcelData(LocalTime value,
                                          ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return new CellData<>(value.format(DateTimeFormatter.ofPattern(Times.Pattern.DEFAULT_TIME)));
        } else {
            return new CellData<>(value.format(DateTimeFormatter.ofPattern(contentProperty.getDateTimeFormatProperty().getFormat())));
        }
    }
}
