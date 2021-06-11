package com.gongbo.excel.example.config;

import com.gongbo.excel.common.result.ResultHandler;
import com.gongbo.excel.example.result.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExportConfiguration {


    @Bean
    public ResultHandler resultBuilder() {
        return new ResultHandler() {
            @Override
            public boolean check(Object result) {
                return result instanceof Result;
            }

            @Override
            public Object success(Object data) {
                return Result.success(data);
            }

            @Override
            public Object error(Throwable e) {
                return Result.fail(e.getMessage());
            }

            @Override
            public Object getData(Object result) {
                return ((Result<?>) result).getData();
            }

        };
    }
}
