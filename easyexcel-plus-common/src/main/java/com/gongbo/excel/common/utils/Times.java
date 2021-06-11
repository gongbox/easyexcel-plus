package com.gongbo.excel.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Times {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Pattern {
        public static final String DEFAULT_YEAR_MONTH = "yyyy-MM";
        public static final String DEFAULT_DATE = "yyyy-MM-dd";
        public static final String DEFAULT_DATE_TIME_NO_SECOND = "yyyy-MM-dd HH:mm";
        public static final String DEFAULT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
        public static final String DEFAULT_TIME_NO_SECOND = "HH:mm";
        public static final String DEFAULT_TIME = "HH:mm:ss";
        public static final String DEFAULT_YEAR = "yyyy";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Formatter {
        public static final DateTimeFormatter DEFAULT_YEAR_MONTH = DateTimeFormatter.ofPattern(Pattern.DEFAULT_YEAR_MONTH);
        public static final DateTimeFormatter DEFAULT_DATE = DateTimeFormatter.ofPattern(Pattern.DEFAULT_DATE);
        public static final DateTimeFormatter DEFAULT_DATE_TIME_NO_SECOND = DateTimeFormatter.ofPattern(Pattern.DEFAULT_DATE_TIME_NO_SECOND);
        public static final DateTimeFormatter DEFAULT_DATE_TIME = DateTimeFormatter.ofPattern(Pattern.DEFAULT_DATE_TIME);
        public static final DateTimeFormatter DEFAULT_TIME_NO_SECOND = DateTimeFormatter.ofPattern(Pattern.DEFAULT_TIME_NO_SECOND);
        public static final DateTimeFormatter DEFAULT_TIME = DateTimeFormatter.ofPattern(Pattern.DEFAULT_TIME);
        public static final DateTimeFormatter DEFAULT_YEAR = DateTimeFormatter.ofPattern(Pattern.DEFAULT_YEAR);
    }


}
