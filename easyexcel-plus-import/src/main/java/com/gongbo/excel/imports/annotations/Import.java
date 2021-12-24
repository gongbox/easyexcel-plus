package com.gongbo.excel.imports.annotations;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

    /**
     * 导入模板
     */
    String template() default "";

    /**
     * 导入模板下载文件名,否则为时间戳
     */
    String templateFilename() default "";

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
    String fileParam() default "file";

    /**
     * 导入模型类
     */
    Class<?> model() default Object.class;

    /**
     * 指定导入方式,否则从配置中取默认值
     */
    String importBy() default "";
}