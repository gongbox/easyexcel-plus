package com.gongbo.excel.imports.handler;


import com.gongbo.excel.imports.entity.ImportContext;

import java.util.Collection;

/**
 * 导入数据转换
 */
public interface ImportDataConvert {

    Object convert(ImportContext importContext, Collection<?> data);

}
