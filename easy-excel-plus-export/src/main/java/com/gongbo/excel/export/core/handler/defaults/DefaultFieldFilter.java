package com.gongbo.excel.export.core.handler.defaults;

import com.gongbo.excel.export.core.handler.FieldFilter;

import java.lang.reflect.Field;

public class DefaultFieldFilter implements FieldFilter {

    @Override
    public boolean predict(Field field) {
        return true;
    }
}