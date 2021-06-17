package com.gongbo.excel.example.config;

import com.gongbo.excel.common.utils.StringUtil;
import com.gongbo.excel.common.utils.Times;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.time.*;
import java.util.Optional;

@Configuration
public class WebConverters {

    @Bean
    public Converter<String, LocalDate> localDateConvert() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String source) {
                return Optional.of(source)
                        .filter(StringUtil::isNotEmpty)
                        .map(v -> LocalDate.parse(v, Times.Formatter.DEFAULT_DATE))
                        .orElse(null);
            }
        };
    }

    @Bean
    public Converter<String, LocalTime> localTimeConvert() {
        return new Converter<String, LocalTime>() {
            @Override
            public LocalTime convert(String source) {
                return Optional.of(source)
                        .filter(StringUtil::isNotEmpty)
                        .map(v -> LocalTime.parse(v, Times.Formatter.DEFAULT_TIME))
                        .orElse(null);
            }
        };
    }

    @Bean
    public Converter<String, LocalDateTime> localDateTimeConvert() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String source) {
                return Optional.of(source)
                        .filter(StringUtil::isNotEmpty)
                        .map(v -> LocalDateTime.parse(v, Times.Formatter.DEFAULT_DATE_TIME))
                        .orElse(null);
            }
        };
    }

    @Bean
    public Converter<String, YearMonth> yearMonthConverter() {
        return new Converter<String, YearMonth>() {
            @Override
            public YearMonth convert(String source) {
                return Optional.of(source)
                        .filter(StringUtil::isNotEmpty)
                        .map(v -> YearMonth.parse(v, Times.Formatter.DEFAULT_YEAR_MONTH))
                        .orElse(null);
            }
        };
    }

    @Bean
    public Converter<String, Year> yearConverter() {
        return new Converter<String, Year>() {
            @Override
            public Year convert(String source) {
                return Optional.of(source)
                        .filter(StringUtil::isNotEmpty)
                        .map(v -> Year.parse(v, Times.Formatter.DEFAULT_YEAR))
                        .orElse(null);
            }
        };
    }

    @Bean
    public Converter<String, Month> monthConverter() {
        return new Converter<String, Month>() {
            @Override
            public Month convert(String source) {
                return Optional.of(source)
                        .filter(StringUtil::isNotEmpty)
                        .map(Integer::parseInt)
                        .map(Month::of)
                        .orElse(null);
            }
        };
    }
}
