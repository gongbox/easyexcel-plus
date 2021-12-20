package com.gongbo.excel.export.annotations;


import com.gongbo.excel.export.core.handler.*;
import com.gongbo.excel.export.core.handler.defaults.*;
import com.gongbo.excel.export.enums.ExcelType;

import java.lang.annotation.*;

@Repeatable(ExcelExports.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelExport {

    /**
     * 导出文件名,否则为时间戳
     */
    String fileName() default "";

    /**
     * 导出sheet名称,为空时默认为Sheet1
     */
    String sheetName() default "";

    /**
     * 导出模板文件名称
     */
    String template() default "";

    /**
     * 导出分组，同一个接口支持多种导出时，可以根据前端请求参数中的export_tag匹配对应的导出注解配置
     */
    String tag() default "";

    /**
     * 导出模型类
     * 值为AutoModel.class（默认）时代表根据方法返回类型查找（要求该方法返回类型为ResponseEntity<Collection<T>> / 或 ResponseEntity<T[]>）
     * 值为ExcelExport.NoneModel.class时代表没有模型类，注意这时导出方式不能为AUTO
     * 值为其他时代表指定为该模型类
     */
    Class<?> modelClass() default AutoModel.class;

    /**
     * 输出文件地址.这时excel将输出到指定目录，且不会通过http响应
     * 格式：固定地址，如：D:\WorkDir\temp\
     * 格式：变量形式，如：${java.io.tmpdir}
     */
    String outputPath() default "";

    /**
     * 动态名称，优先级大于fileName
     */
    Class<? extends FileNameConvert> fileNameConvert() default DefaultFileNameConvert.class;

    /**
     * 字段过滤
     */
    Class<? extends FieldFilter> fieldFilter() default DefaultFieldFilter.class;

    /**
     * 数据转换
     */
    Class<? extends ExportDataConvert> dataConvert() default DefaultExportDataConvert.class;

    /**
     * 生成文件执行器
     */
    Class<? extends WriteHandler> writeHandler() default DefaultWriteHandler.class;

    /**
     *
     */
    Class<? extends ExportLifecycle> afterExportHandler() default DefaultExportLifecycle.class;

    /**
     * 导出excel文件格式
     */
    ExcelType excelType() default ExcelType.AUTO;

    /**
     * 是否执行公式
     */
    boolean formula() default true;

    /**
     * 是否响应请求结果
     */
    boolean responseResult() default false;

    /**
     * 没有模型类
     */
    final class NoneModel {
    }

    /**
     * 自动获取模型类
     * 1，模板导出时，模型类为NoneModel
     * 2，非模板导出时，模型类根据方法返回获取
     */
    final class AutoModel {
    }
}