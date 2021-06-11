package com.gongbo.excel.imports.annotations;

import com.gongbo.excel.imports.config.ImportConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 配置开启自动导入
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ImportConfig.class)
@Documented
public @interface EnableAutoImport {

}
