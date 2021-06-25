package com.gongbo.excel.example.view;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
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
@ColumnWidth(12)
@ContentRowHeight(18)
public class ExportDemoView {

    @ExcelProperty("文本")
    private String text = RandomUtil.randomString(8);

    @ExcelProperty("整数")
    private Integer integerValue = RandomUtil.randomInt(10000);

    @ExcelProperty("浮点数")
    private Float floatValue = (float) RandomUtil.randomDouble(-10000, 10000);

    @ExcelProperty("长浮点数")
    private Double doubleValue = RandomUtil.randomDouble(-10000, 10000);

    @ExcelProperty("定点数")
    private BigDecimal bigDecimal = RandomUtil.randomBigDecimal(BigDecimal.valueOf(10_000));

    @ExcelProperty("日期")
    private LocalDate localDate = LocalDate.now();

    @ExcelProperty("日期时间")
    @ColumnWidth(20)
    private LocalDateTime localDateTime = LocalDateTime.now();

    @ExcelProperty("时间")
    @ColumnWidth(20)
    private Date date = new Date();

    public static List<ExportDemoView> data() {
        return Stream.generate(ExportDemoView::new)
                .limit(new Random().nextInt(10))
                .collect(Collectors.toList());
    }
}
