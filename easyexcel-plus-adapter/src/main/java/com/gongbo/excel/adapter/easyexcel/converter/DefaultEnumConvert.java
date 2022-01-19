package com.gongbo.excel.adapter.easyexcel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class DefaultEnumConvert implements Converter<Object> {
    @Override
    public Class supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Object convertToJavaData(ReadCellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String stringValue = Optional.ofNullable(cellData.getStringValue()).map(String::trim).orElse("");

        //获取枚举类型
        Class<?> type = excelContentProperty.getField().getType();

        //查找对应的值字段
        Field field = Arrays.stream(type.getDeclaredFields())
                .filter(f -> {
                    ExcelValue excelValue = f.getAnnotation(ExcelValue.class);
                    if (excelValue == null) {
                        return false;
                    }
                    return excelValue.value() == ExcelValue.Support.READ || excelValue.value() == ExcelValue.Support.ALL;
                }).findAny()
                .orElseThrow(() -> new IllegalArgumentException("在枚举类上没有找到支持输入的ExcelValue注解"));
        //允许访问私有属性
        field.setAccessible(true);
        for (Object enumConstant : type.getEnumConstants()) {
            Object value = field.get(enumConstant);
            if (Objects.equals(String.valueOf(value), stringValue)) {
                return enumConstant;
            }
        }
        throw new IllegalStateException("没有匹配到对应的枚举值：" + stringValue);
    }

    @Override
    public WriteCellData convertToExcelData(Object o, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        Class<?> type = excelContentProperty.getField().getType();
        Field[] declaredFields = type.getDeclaredFields();

        Field field = Arrays.stream(declaredFields)
                .filter(f -> {
                    ExcelValue excelValue = f.getAnnotation(ExcelValue.class);
                    if (excelValue == null) {
                        return false;
                    }
                    return excelValue.value() == ExcelValue.Support.WRITE || excelValue.value() == ExcelValue.Support.ALL;

                }).findAny()
                .orElseThrow(() -> new IllegalArgumentException("在枚举类上没有找到支持输出的ExcelValue注解"));

        //允许访问私有属性
        field.setAccessible(true);
        Object value = field.get(o);
        return new WriteCellData<>(String.valueOf(value));
    }

}
