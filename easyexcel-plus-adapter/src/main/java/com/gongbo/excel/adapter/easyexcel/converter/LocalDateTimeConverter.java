package com.gongbo.excel.adapter.easyexcel.converter;


import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
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
    public LocalDateTime convertToJavaData(ReadCellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return LocalDateTime.parse(cellData.getStringValue(), DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_19));
        } else {
            return LocalDateTime.parse(cellData.getStringValue(),
                    DateTimeFormatter.ofPattern(contentProperty.getDateTimeFormatProperty().getFormat()));
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(LocalDateTime value,
                                               ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
            return new WriteCellData<>(value.format(DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_19)));
        } else {
            return new WriteCellData<>(value.format(DateTimeFormatter.ofPattern(contentProperty.getDateTimeFormatProperty().getFormat())));
        }
    }
}
