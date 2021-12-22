package com.gongbo.excel.export.custom;


import com.gongbo.excel.export.entity.ExportContext;

import java.util.List;

/**
 * 导出数据转换
 */
public interface ExportDataConvert {

    /**
     * @param result 接口返回的数据（
     * @return 返回的是写入excel的数据
     */
    List<?> convert(ExportContext exportContext, Object result);

}
