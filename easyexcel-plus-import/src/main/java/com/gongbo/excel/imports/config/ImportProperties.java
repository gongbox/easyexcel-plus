package com.gongbo.excel.imports.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
public class ImportProperties {
    /**
     * 默认Sheet名称
     */
    private String defaultSheetName = "Sheet1";

    /**
     * 默认导入方式
     */
    private String defaultImportBy = "easy_excel";

    /**
     * 读取超时时间，默认为1分钟
     */
    private Integer readTimeout = 60 * 1000;

}
