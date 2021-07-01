package com.gongbo.excel.export.config;

import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
public class ExportProperties {
    /**
     * controller接口响应类名
     */
    private String responseClassName;

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
}
