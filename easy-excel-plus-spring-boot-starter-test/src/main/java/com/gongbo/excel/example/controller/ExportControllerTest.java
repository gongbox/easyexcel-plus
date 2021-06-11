package com.gongbo.excel.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.gongbo.excel.example.result.Result;
import com.gongbo.excel.example.view.ExportDemoView;
import com.gongbo.excel.export.annotations.EnableExport;
import com.gongbo.excel.export.core.ExportContextHolder;
import com.gongbo.excel.export.core.handler.ExportDataConvert;
import com.gongbo.excel.export.core.handler.FieldFilter;
import com.gongbo.excel.export.core.handler.FileNameConvert;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.entity.fill.ExportFillData;
import com.gongbo.excel.export.enums.ExcelType;
import com.gongbo.excel.export.utils.ExportFormulas;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 电量指标 前端控制器
 * </p>
 *
 * @author gongbo
 * @since 2021-01-04
 */
@Api(tags = "export")
//@RestController
@RequestMapping(value = "/")
@Validated
@Deprecated
public class ExportControllerTest {

    @ApiOperation("通用导出-file")
    @GetMapping(value = "commonFile")
    //通用导出-生成文件
    @EnableExport(outputPath = "D:\\WorkDir\\temp\\file")
    public Result<List<ExportDemoView>> commonFile() {
        return Result.success(ExportDemoView.data());
    }

    @ApiOperation("通用导出-默认")
    @GetMapping(value = "commonDefault")
    //默认导出
    @EnableExport
    public Result<List<ExportDemoView>> commonDefault() {
        return Result.success(ExportDemoView.data());
    }

    @ApiOperation("通用导出")
    @GetMapping(value = "common")
    //固定名称
    @EnableExport(fileName = "通用导出1", tag = "constant_file_name")
    //动态名称
    @EnableExport(fileNameConvert = CustomFileNameConvert.class, tag = "dynamic_file_name")
    //业务中修改名称
    @EnableExport(tag = "business_dynamic_file_name")
    //固定sheet名称
    @EnableExport(sheetName = "Sheet0", tag = "constant_sheet_name")
    //业务中修改sheet名称
    @EnableExport(tag = "business_sheet_name")
    //字段过滤
    @EnableExport(tag = "field_filter", fieldFilter = CustomFieldFilter.class)
    //导出文件类型
    @EnableExport(tag = "excel_type_xls", excelType = ExcelType.XLS)
    //导出文件类型
    @EnableExport(tag = "excel_type_xlsx", excelType = ExcelType.XLSX)
    //数据处理
    @EnableExport(tag = "data_convert", dataConvert = CustomExportDataConvert.class)
    public Result<List<ExportDemoView>> common() {
        if (ExportContextHolder.isExportExcel()) {
            if ("business_dynamic_file_name" .equals(ExportContextHolder.getContext().getExportParam().getExportTag())) {
                ExportContextHolder.getContext().setFileName("业务中修改文件名称");
            }
            if ("business_sheet_name" .equals(ExportContextHolder.getContext().getExportParam().getExportTag())) {
                ExportContextHolder.getContext().setSheetName("业务中修改Sheet名称");
            }
        }
        return Result.success(ExportDemoView.data());
    }

    @ApiOperation("通用模板导出")
    @GetMapping(value = "template")
    //模板导出1（单个Sheet）
    @EnableExport(template = "template1.xls", tag = "template1", dataConvert = Template1DataConvert.class)
    //模板导出2（多个Sheet）
    @EnableExport(template = "template2.xls", tag = "template2", dataConvert = Template2DataConvert.class)
    public Result<List<ExportDemoView>> template() {
        return Result.success(ExportDemoView.data());
    }

    @ApiOperation("通用模板导出")
    @GetMapping(value = "template3")
    //模板导出3（只有导出功能）
    @EnableExport(template = "template2.xls")
    public Result<List<ExportFillData>> template3() {
        ExportFillData exportFillData1 = ExportFillData.builder()
                .sheetName("Sheet1")
                .data(ExportDemoView.data())
                .build();

        Map<String, String> map = new HashMap<>();

        map.put("name", "名称");
        map.put("date_start", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        map.put("date_end", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        ExportFillData exportFillData2 = ExportFillData.builder()
                .sheetNo(0)
                .data(map)
                .build();

        return Result.success(Lists.newArrayList(exportFillData1, exportFillData2));
    }

    @ApiOperation("模板导出-公式")
    @GetMapping(value = "template5")
    //模板导出3-公式
    @EnableExport(template = "template5.xls")
    public Result<List<ExportFillData>> template5() {

        List<Map<String, Object>> list = Stream.generate(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "名称");
            map.put("value1", RandomUtil.randomBigDecimal(BigDecimal.TEN));
            map.put("value2", RandomUtil.randomBigDecimal(BigDecimal.TEN));

            return map;
        }).limit(RandomUtil.randomInt(10))
                .collect(Collectors.toList());

        ExportFillData exportFillData1 = ExportFillData.builder()
                .sheetName("Sheet1")
                .fillConfig(FillConfig.builder().forceNewRow(true).build())
                .data(new FillWrapper("data1", list))
                .build();
        ExportFillData exportFillData2 = ExportFillData.builder()
                .fillConfig(FillConfig.builder().forceNewRow(true).build())
                .sheetName("Sheet2")
                .data(new FillWrapper("data2", list))
                .build();

        int start = 4;
        int end = start + (CollUtil.isEmpty(list) ? 0 : list.size() - 1);

        Map<String, Object> constantMap1 = new HashMap<>();

        constantMap1.put("date", LocalDate.now().toString());
        constantMap1.put("data1_value1_avg", ExportFormulas.averageColumnFormula("B", start, end));
        constantMap1.put("data1_value2_avg", ExportFormulas.averageColumnFormula("C", start, end));

        ExportFillData exportFillData3 = ExportFillData.builder()
                .sheetName("Sheet1")
                .data(constantMap1)
                .build();

        Map<String, Object> constantMap2 = new HashMap<>();

        constantMap2.put("date", LocalDate.now().toString());
        constantMap2.put("data2_start", start);
        constantMap2.put("data2_end", end);

        ExportFillData exportFillData4 = ExportFillData.builder()
                .sheetName("Sheet2")
                .data(constantMap2)
                .build();

        return Result.success(Lists.newArrayList(exportFillData1, exportFillData2, exportFillData3, exportFillData4));
    }

    public static class CustomFileNameConvert implements FileNameConvert {
        @Override
        public String apply(String fileName) {
            return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        }
    }

    public static class CustomFieldFilter implements FieldFilter {

        @Override
        public boolean predict(Field field) {
            return RandomUtil.randomBoolean();
        }
    }

    public static class CustomExportDataConvert implements ExportDataConvert {

        @Override
        public List<?> convert(ExportContext exportContext, Object data) {
            Result<?> responseEntity = (Result<?>) data;

            List<ExportDemoView> list = (List<ExportDemoView>) responseEntity.getData();

            for (int i = 0; i < 20; i++) {
                list.add(new ExportDemoView());
            }

            return list;
        }
    }


    public static class Template1DataConvert implements ExportDataConvert {
        @Override
        public List<?> convert(ExportContext exportContext, Object data) {
            Result<?> responseEntity = (Result<?>) data;

            ExportFillData exportFillData1 = ExportFillData.builder()
                    .data(responseEntity.getData())
                    .build();

            Map<String, String> map = new HashMap<>();

            map.put("name", "名称");
            map.put("date_start", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            map.put("date_end", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            ExportFillData exportFillData2 = ExportFillData.builder()
                    .data(map)
                    .build();

            return Lists.newArrayList(exportFillData1, exportFillData2);
        }
    }

    public static class Template2DataConvert implements ExportDataConvert {
        @Override
        public List<?> convert(ExportContext exportContext, Object data) {
            Result<?> responseEntity = (Result<?>) data;

            ExportFillData exportFillData1 = ExportFillData.builder()
                    .sheetName("Sheet1")
                    .data(responseEntity.getData())
                    .build();

            Map<String, String> map = new HashMap<>();

            map.put("name", "名称");
            map.put("date_start", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            map.put("date_end", LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            ExportFillData exportFillData2 = ExportFillData.builder()
                    .sheetNo(0)
                    .data(map)
                    .build();

            return Lists.newArrayList(exportFillData1, exportFillData2);
        }
    }
}
