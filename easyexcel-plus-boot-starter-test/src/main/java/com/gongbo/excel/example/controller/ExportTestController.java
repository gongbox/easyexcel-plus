package com.gongbo.excel.example.controller;

import cn.hutool.core.util.RandomUtil;
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
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "export")
@RestController
@RequestMapping(value = "/")
@Validated
public class ExportTestController {
    @GetMapping(value = "test1")
    @EnableExport
    public Result<List<ExportDemoView>> test1() {
        return Result.success(ExportDemoView.data());
    }

    // 导出-设置导出文件名称：
    @GetMapping(value = "test2")
    @EnableExport(fileName = "文件名称")
    public Result<List<ExportDemoView>> test2() {
        return Result.success(ExportDemoView.data());
    }

    //导出-动态设置文件名称：
    @GetMapping(value = "test3")
    @EnableExport(fileNameConvert = CustomFileNameConvert.class)
    public Result<List<ExportDemoView>> test3() {
        return Result.success(ExportDemoView.data());
    }

    public static class CustomFileNameConvert implements FileNameConvert {
        @Override
        public String apply(String fileName) {
            return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        }
    }

    @GetMapping(value = "test4")
    @EnableExport
    public Result<List<ExportDemoView>> test4() {
        if (ExportContextHolder.isExportExcel()) {
            ExportContextHolder.getContext().setFileName("动态文件名称");
        }
        return Result.success(ExportDemoView.data());
    }

    //导出-固定Sheet名称：
    @GetMapping(value = "test5")
    @EnableExport(sheetName = "Sheet0")
    public Result<List<ExportDemoView>> test5() {
        return Result.success(ExportDemoView.data());
    }

    //导出-动态设置Sheet名称：
    @GetMapping(value = "test6")
    @EnableExport
    public Result<List<ExportDemoView>> test6() {
        if (ExportContextHolder.isExportExcel()) {
            ExportContextHolder.getContext().setSheetName("业务中修改Sheet名称");
        }
        return Result.success(ExportDemoView.data());
    }

    //导出到固定文件夹：
    @GetMapping(value = "test7")
    @EnableExport(outputPath = "D:\\WorkDir\\temp\\file")
    public Result<List<ExportDemoView>> test7() {
        return Result.success(ExportDemoView.data());
    }

    //导出-字段过滤：
    @GetMapping(value = "test8")
    @EnableExport(fieldFilter = CustomFieldFilter.class)
    public Result<List<ExportDemoView>> test8() {
        return Result.success(ExportDemoView.data());
    }

    public static class CustomFieldFilter implements FieldFilter {
        @Override
        public boolean predict(Field field) {
            return RandomUtil.randomBoolean();
        }
    }

    //导出-设置导出文件格式：
    @GetMapping(value = "test9")
    @EnableExport(excelType = ExcelType.XLS)
    public Result<List<ExportDemoView>> test9() {
        return Result.success(ExportDemoView.data());
    }

    //导出-数据转换：
    @GetMapping(value = "test10")
    @EnableExport(dataConvert = CustomExportDataConvert.class)
    public Result<List<ExportDemoView>> test10() {
        return Result.success(ExportDemoView.data());
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

    //导出-同一接口多种导出方式：
    @GetMapping(value = "test11")
    @EnableExport(tag = "xls", excelType = ExcelType.XLS)
    @EnableExport(tag = "xlsx", excelType = ExcelType.XLSX)
    public Result<List<ExportDemoView>> test11() {
        return Result.success(ExportDemoView.data());
    }

    @GetMapping(value = "testTemplate1")
    @EnableExport(template = "template1.xls", dataConvert = Template1DataConvert.class)
    public Result<List<ExportDemoView>> testTemplate1() {
        return Result.success(ExportDemoView.data());
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

    //导出-模版导出（多个Sheet）：
    @GetMapping(value = "testTemplate2")
    @EnableExport(template = "template2.xls", dataConvert = Template2DataConvert.class)
    public Result<List<ExportDemoView>> testTemplate2() {
        return Result.success(ExportDemoView.data());
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
