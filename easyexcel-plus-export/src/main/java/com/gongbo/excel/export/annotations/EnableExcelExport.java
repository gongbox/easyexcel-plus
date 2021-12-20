package com.gongbo.excel.export.annotations;


import com.gongbo.excel.export.advise.ExportAdvise;
import com.gongbo.excel.export.config.ExportProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 配置开启自动导出
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ExportAdvise.class, ExportProperties.class})
@Documented
public @interface EnableExcelExport {

}
