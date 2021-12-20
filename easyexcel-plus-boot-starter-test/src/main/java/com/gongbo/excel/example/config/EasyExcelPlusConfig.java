package com.gongbo.excel.example.config;

import com.gongbo.excel.common.result.ResultHandler;
import com.gongbo.excel.example.result.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EasyExcelPlusConfig {

    @Bean
    public ResultHandler<Result> resultBuilder() {
        return new ResultHandler<Result>() {
            @Override
            public Class<Result> resultClass() {
                return Result.class;
            }

            @Override
            public Object getData(Result result) {
                return result.getData();
            }
        };
    }
}
