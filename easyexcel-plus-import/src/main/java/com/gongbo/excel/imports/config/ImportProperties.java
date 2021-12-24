package com.gongbo.excel.imports.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ImportProperties {
    /**
     * 默认Sheet名称
     */
    private String defaultSheetName = "Sheet1";

    /**
     * 模板文件路径
     */
    private String templateDir = "";

    /**
     * 默认导入方式
     */
    private String defaultImportBy = "easy_excel";

    /**
     * 读取excel超时时间，不设置或设置为0时无读取时间限制
     */
    private Integer readTimeout;

}
