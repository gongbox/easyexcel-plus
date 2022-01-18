package com.gongbo.excel.example.config;

import com.gongbo.excel.example.result.Result;
import com.gongbo.excel.export.core.resulthandler.DefaultResultHandler;
import com.gongbo.excel.export.core.resulthandler.ResultHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EasyExcelPlusConfig {

    @Bean
    public ResultHandler resultBuilder() {
        return new DefaultResultHandler() {
            @Override
            public Class<?> resultClass() {
                return Result.class;
            }

            @Override
            public Object getResultData(Object result) {
                if (result instanceof Result) {
                    return ((Result<?>) result).getData();
                }
                return super.getResultData(result);
            }
        };
    }
}
