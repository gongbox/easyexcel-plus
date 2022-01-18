package com.gongbo.excel.adapter.easyexcel.converter;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExcelValue {

    /**
     * 支持方式
     */
    Support value() default Support.ALL;

    enum Support {
        /**
         * 读取
         */
        READ,
        /**
         * 输出
         */
        WRITE,
        /**
         * 读取、输出
         */
        ALL
    }
}
