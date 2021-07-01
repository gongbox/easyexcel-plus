package com.gongbo.excel;

import com.gongbo.excel.export.config.ExportProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExportPropertiesConfiguration {

    @Bean
    @ConfigurationProperties("easyexcel-plus.export")
    @ConditionalOnMissingBean
    public ExportProperties exportProperties() {
        return new ExportProperties();
    }
}
