package com.gongbo.excel.imports.config;

import com.gongbo.excel.imports.advise.ImportAdvise;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Import(ImportAdvise.class)
public class ImportConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ImportMappingJackson2HttpMessageConverter());
    }

}
