package com.gongbo.excel;

import com.gongbo.excel.export.config.ExportConfig;
import com.gongbo.excel.export.config.ExportProperties;
import com.gongbo.excel.imports.config.ImportConfig;
import com.gongbo.excel.imports.config.ImportProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ImportConfig.class, ExportConfig.class})
public class EasyExcelPlusConfiguration {

    @Bean
    @ConfigurationProperties("easyexcel-plus.export")
    @ConditionalOnMissingBean
    public ExportProperties exportProperties() {
        return new ExportProperties();
    }

    @Bean
    @ConfigurationProperties("easyexcel-plus.import")
    @ConditionalOnMissingBean
    public ImportProperties importProperties() {
        return new ImportProperties();
    }
}
