package com.gongbo.excel.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.*;
import java.util.Date;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket webApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .directModelSubstitute(LocalDateTime.class, Date.class)
                .directModelSubstitute(LocalDate.class, Date.class)
                .directModelSubstitute(LocalTime.class, Date.class)
                .directModelSubstitute(Year.class, Integer.class)
                .directModelSubstitute(Month.class, Integer.class)
                .directModelSubstitute(YearMonth.class, String.class)
                .groupName("EasyExcelPlus")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.gongbo.excel.example.controller"))
                .build();
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("EasyExcelPlus")
                .description("EasyExcelPlus接口文档")
                .version("1.0")
                .build();
    }
}
