package com.gongbo.excel.imports.entity;


import com.gongbo.excel.imports.annotations.ExcelImport;
import com.gongbo.excel.imports.config.ImportProperties;
import com.gongbo.excel.imports.param.ImportParam;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportContext {

    /**
     * 导入请求参数
     */
    private ImportParam importParam;

    /**
     * 导入注解信息
     */
    private ExcelImport excelImport;

    /**
     * 导入模板文件名
     */
    private String templateFilename;

    /**
     * 导入sheet名称
     */
    private String sheetName;

    /**
     * 导入sheet位置
     */
    private Integer sheetNo;

    /**
     * 导入目标参数位置
     */
    private Integer targetArgumentIndex;

    /**
     * 导入接收参数容器类型
     */
    private Class<?> targetArgumentContainerClass;

    /**
     * 导入对应模型类
     */
    private Class<?> targetArgumentClass;

    /**
     * 导入文件参数名称
     */
    private String fileParamName;

    /**
     * 配置信息
     */
    private ImportProperties importProperties;

}
