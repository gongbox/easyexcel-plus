package com.gongbo.excel.export.custom.defaults;


import com.gongbo.excel.export.custom.FieldFilter;
import com.gongbo.excel.export.entity.ExportFieldInfo;

public class DefaultFieldFilter implements FieldFilter {

    @Override
    public boolean predict(ExportFieldInfo fieldInfo) {
        return true;
    }
}