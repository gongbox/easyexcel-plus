package com.gongbo.excel.imports.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义HttpMessageConverter，支持以multipart/form-data格式（excel解析）传json数据
 */
public class ImportMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public ImportMappingJackson2HttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.json().build());
    }

    public ImportMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
        this.setSupportedMediaTypes(supportedMediaTypes);
    }

}
