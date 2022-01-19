package com.gongbo.excel.example.config;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ConverterKeyBuild;
import com.gongbo.excel.adapter.easyexcel.converter.LocalDateConverter;
import com.gongbo.excel.adapter.easyexcel.converter.LocalDateTimeConverter;
import com.gongbo.excel.adapter.easyexcel.converter.LocalTimeConverter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import static com.alibaba.excel.converters.DefaultConverterLoader.loadAllConverter;
import static com.alibaba.excel.converters.DefaultConverterLoader.loadDefaultWriteConverter;

@NoArgsConstructor
@Configuration
public class EasyExcelConfig implements InitializingBean {


    private static void putAllConverter(Converter converter) {
        loadAllConverter().put(ConverterKeyBuild.buildKey(converter.supportJavaTypeKey(), converter.supportExcelTypeKey()),
                converter);
    }

    private static void putWriteConverter(Converter converter) {
        loadDefaultWriteConverter().put(ConverterKeyBuild.buildKey(converter.supportJavaTypeKey()), converter);
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
