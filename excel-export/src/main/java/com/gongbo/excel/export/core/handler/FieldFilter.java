package com.gongbo.excel.export.core.handler;

import java.lang.reflect.Field;

/**
 * 字段过滤
 */
public interface FieldFilter {

    /**
     * @param field
     * @return
     */
    boolean predict(Field field);

}
