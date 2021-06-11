package com.gongbo.excel.example.view;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class ExportDemoView {

    @ExcelProperty
    private String text = "text";

    @ExcelProperty
    private Integer integerValue = 12;

    @ExcelProperty
    private Float floatValue = 13.0f;

    @ExcelProperty
    private Double doubleValue = 213.4566;

    @ExcelProperty
    private BigDecimal bigDecimal = BigDecimal.TEN;

    @ExcelProperty
    private LocalDate localDate = LocalDate.now();

    @ExcelProperty
    private LocalDateTime localDateTime = LocalDateTime.now();

    @ExcelProperty
    private Date date = new Date();

    public static List<ExportDemoView> data() {
        return Stream.generate(ExportDemoView::new)
                .limit(new Random().nextInt(10))
                .collect(Collectors.toList());
    }
}
