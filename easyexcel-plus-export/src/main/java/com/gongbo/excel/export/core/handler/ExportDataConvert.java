package com.gongbo.excel.export.core.handler;


import com.gongbo.excel.export.entity.ExportContext;

import java.util.List;

/**
 * 导出数据转换
 */
public interface ExportDataConvert {

    /**
     * @param responseEntity 接口返回的数据（类型为ResponseEntity）
     * @return 返回的是写入excel的数据，比如ResponseEntity.data
     */
    List<?> convert(ExportContext exportContext, Object responseEntity);

}
