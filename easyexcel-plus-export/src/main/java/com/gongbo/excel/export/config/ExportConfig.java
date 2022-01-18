package com.gongbo.excel.export.config;

import com.gongbo.excel.export.advise.ExportAdvise;
import com.gongbo.excel.export.core.resulthandler.DefaultResultHandler;
import org.springframework.context.annotation.Import;

@Import({ExportAdvise.class, DefaultResultHandler.class})
public class ExportConfig {

}
