package com.gongbo.excel.imports.annotations;

import java.lang.annotation.*;

/**
 * 导入目标，标注在接收excel解析参数上
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ImportTarget {
}