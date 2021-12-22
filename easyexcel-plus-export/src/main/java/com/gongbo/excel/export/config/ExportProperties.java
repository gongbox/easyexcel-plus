package com.gongbo.excel.export.config;

import com.gongbo.excel.common.result.ResultHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
public class ExportProperties {
    /**
     * 默认Sheet名称
     */
    private String defaultSheetName = "Sheet1";

    /**
     * 填充key名
     */
    private String fillKey = "fill_data";

    /**
     * 模板文件路径
     */
    private String templateDir = "";

    /**
     * 公式开始前缀
     */
    private String formulaPrefix = "#formula";

    /**
     * 默认导出excel格式
     */
    private String defaultExcelType = "xlsx";

    /**
     * 默认导出方式
     */
    private String defaultExportBy = "easy_excel";

    @Autowired
    private ResultHandler<?> resultHandler;
}
