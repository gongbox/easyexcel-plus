package com.gongbo.excel.export.custom;

import com.gongbo.excel.export.entity.ExportFieldInfo;

/**
 * 字段过滤
 */
public interface FieldFilter {

    /**
     * @param fieldInfo
     * @return
     */
    boolean predict(ExportFieldInfo fieldInfo);

}
