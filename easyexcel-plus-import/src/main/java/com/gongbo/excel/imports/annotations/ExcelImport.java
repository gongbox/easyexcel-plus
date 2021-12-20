package com.gongbo.excel.imports.annotations;

import com.gongbo.excel.imports.core.ImportHandlers;
import com.gongbo.excel.imports.handler.DownloadTemplateHandler;
import com.gongbo.excel.imports.handler.ImportDataConvert;
import com.gongbo.excel.imports.handler.ReadHandler;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelImport {

    /**
     * 导入模板文件名,否则为时间戳
     */
    String templateFileName() default "";

    /**
     * 导入sheet名称,为空时默认为Sheet1
     */
    String sheetName() default "";

    /**
     * 导入sheet位置，从0开始
     */
    int sheetNo() default -1;

    /**
     * 请求参数名称
     */
    String paramName() default "file";

    /**
     * 导入模型类
     */
    Class<?> modelClass() default Object.class;

    /**
     * 数据转换
     */
    Class<? extends ImportDataConvert> dataConvert() default ImportHandlers.DefaultImportDataConvert.class;

    /**
     * 读取文件执行器
     */
    Class<? extends ReadHandler> readHandler() default ImportHandlers.DefaultReadHandler.class;

    /**
     * 读取文件执行器
     */
    Class<? extends DownloadTemplateHandler> downloadTemplateHandler() default ImportHandlers.DefaultDownloadTemplateHandler.class;

}