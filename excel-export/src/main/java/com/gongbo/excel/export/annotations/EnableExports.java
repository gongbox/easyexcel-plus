package com.gongbo.excel.export.annotations;

import java.lang.annotation.*;

/**
 * 多重注解支持
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableExports {
    EnableExport[] value();
}
