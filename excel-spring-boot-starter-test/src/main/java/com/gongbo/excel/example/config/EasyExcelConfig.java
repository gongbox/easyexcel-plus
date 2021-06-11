package com.gongbo.excel.example.config;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ConverterKeyBuild;
import com.alibaba.excel.converters.DefaultConverterLoader;
import com.gongbo.excel.common.converter.LocalDateConverter;
import com.gongbo.excel.common.converter.LocalDateTimeConverter;
import com.gongbo.excel.common.converter.LocalTimeConverter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

@NoArgsConstructor
@Configuration
public class EasyExcelConfig implements InitializingBean {


    private static void putWriteConverter(Converter<?> converter) {
        DefaultConverterLoader.loadDefaultWriteConverter().put(ConverterKeyBuild.buildKey(converter.supportJavaTypeKey()), converter);
    }


    private static void putAllConverter(Converter<?> converter) {
        DefaultConverterLoader.loadAllConverter().put(ConverterKeyBuild.buildKey(converter.supportJavaTypeKey(), converter.supportExcelTypeKey()),
                converter);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();
        LocalDateConverter localDateConverter = new LocalDateConverter();
        LocalTimeConverter localTimeConverter = new LocalTimeConverter();

        putWriteConverter(localDateConverter);
        putWriteConverter(localDateTimeConverter);
        putWriteConverter(localTimeConverter);

        putAllConverter(localDateConverter);
        putAllConverter(localDateTimeConverter);
        putAllConverter(localTimeConverter);
    }
}
