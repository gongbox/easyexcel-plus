package com.gongbo.excel.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.gongbo.excel.common.utils.Times;
import com.gongbo.excel.example.result.Result;
import com.gongbo.excel.example.view.ExportDemoView;
import com.gongbo.excel.export.annotations.Export;
import com.gongbo.excel.export.constants.ExportExcelType;
import com.gongbo.excel.export.core.ExportContextHolder;
import com.gongbo.excel.export.custom.ExportDataConvert;
import com.gongbo.excel.export.custom.FieldFilter;
import com.gongbo.excel.export.custom.FileNameConvert;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.entity.ExportFieldInfo;
import com.gongbo.excel.export.entity.ExportFillData;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "export")
@RestController
@RequestMapping(value = "/export")
@Validated
public class ExportTestController {
    /**
     * 导出-简单导出
     */
    @GetMapping(value = "test-normal")
    @Export
    public Result<List<ExportDemoView>> testNormal() {
        return Result.success(ExportDemoView.data());
    }

    /**
     * 导出-设置导出文件名称
     */
    @GetMapping(value = "test-fileName")
    @Export(fileName = "文件名称")
    public Result<List<ExportDemoView>> testFilename() {
        return Result.success(ExportDemoView.data());
    }

    /**
     * 导出-动态设置文件名称
     */
    @GetMapping(value = "test-fileName-convert")
    @Export(fileNameConvert = CustomFileNameConvert.class)
    public Result<List<ExportDemoView>> testFileNameConvert() {
        return Result.success(ExportDemoView.data());
    }

    public static class CustomFileNameConvert implements FileNameConvert {
        @Override
        public String apply(String fileName) {
            return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        }
    }

    /**
     *
     */
    @GetMapping(value = "test-fileName-business")
    @Export
    public Result<List<ExportDemoView>> testFileNameBusiness() {
        if (ExportContextHolder.isExportExcel()) {
            ExportContextHolder.getContext().setFileName("动态文件名称");
        }
        return Result.success(ExportDemoView.data());
    }

    /**
     * 导出-固定Sheet名称
     */
    @GetMapping(value = "test-sheetName")
    @Export(sheetName = "Sheet0")
    public Result<List<ExportDemoView>> testSheetName() {
        return Result.success(ExportDemoView.data());
    }

    /**
     * 导出-动态设置Sheet名称
     */
    @GetMapping(value = "test-sheetName-business")
    @Export
    public Result<List<ExportDemoView>> testSheetNameBusiness() {
        if (ExportContextHolder.isExportExcel()) {
            ExportContextHolder.getContext().setSheetName("业务中修改Sheet名称");
        }
        return Result.success(ExportDemoView.data());
    }

    /**
     * 导出到固定文件夹
     */
    @GetMapping(value = "test-out-path")
    @Export(outputPath = "D:\\WorkDir\\temp\\file")
    public Result<List<ExportDemoView>> testOutPath() {
        return Result.success(ExportDemoView.data());
    }

    /**
     * 导出-字段过滤
     */
    @GetMapping(value = "test-filter")
    @Export(fieldFilter = CustomFieldFilter.class)
    public Result<List<ExportDemoView>> testFilter() {
        return Result.success(ExportDemoView.data());
    }

    public static class CustomFieldFilter implements FieldFilter {
        @Override
        public boolean predict(ExportFieldInfo fieldInfo) {
            return RandomUtil.randomBoolean();
        }
    }

    /**
     * 导出-设置导出文件格式
     */
    @GetMapping(value = "test-excelType")
    @Export(excelType = ExportExcelType.XLS)
    public Result<List<ExportDemoView>> testExcelType() {
        return Result.success(ExportDemoView.data());
    }

    /**
     * 导出-数据转换
     */
    @GetMapping(value = "test-dataConvert")
    @Export(dataConvert = CustomExportDataConvert.class)
    public Result<List<ExportDemoView>> testDataConvert() {
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

    /**
     * 导出-同一接口多种导出方式
     */
    @GetMapping(value = "test-tag")
    @Export(tag = "xls", excelType = ExportExcelType.XLS)
    @Export(tag = "xlsx", excelType = ExportExcelType.XLSX)
    public Result<List<ExportDemoView>> testTag() {
        return Result.success(ExportDemoView.data());
    }

    /**
     * 导出-简单模版导出
     */
    @GetMapping(value = "test-template-simple")
    @Export(template = "template-simply.xlsx")
    public Result<List<ExportDemoView>> testTemplateSimple() {
        return Result.success(ExportDemoView.data());
    }

    /**
     * 导出-单个Sheet模版导出
     */
    @GetMapping(value = "test-template-single-sheet")
    @Export(template = "template-single-sheet.xlsx", dataConvert = TemplateSingleSheetDataConvert.class)
    public Result<List<ExportDemoView>> testTemplateSingleSheet() {
        return Result.success(ExportDemoView.data());
    }

    public static class TemplateSingleSheetDataConvert implements ExportDataConvert {
        @Override
        public List<?> convert(ExportContext exportContext, Object data) {
            Result<?> responseEntity = (Result<?>) data;

            ExportFillData exportFillData1 = ExportFillData.builder()
                    .data(responseEntity.getData())
                    .build();

            Map<String, String> map = new HashMap<>();
            map.put("name", "名称");
            map.put("date", LocalDate.now().format(Times.Formatter.DEFAULT_DATE));
            ExportFillData exportFillData2 = ExportFillData.builder()
                    .data(map)
                    .build();

            return Lists.newArrayList(exportFillData1, exportFillData2);
        }
    }

    //导出-模版导出（多个Sheet）：
    @GetMapping(value = "test-template-much-sheet")
    @Export(template = "template-much-sheet.xlsx", dataConvert = TemplateMuchSheetDataConvert.class)
    public Result<List<ExportDemoView>> testTemplateMuchSheet() {
        return Result.success(ExportDemoView.data());
    }

    public static class TemplateMuchSheetDataConvert implements ExportDataConvert {
        @Override
        public List<?> convert(ExportContext exportContext, Object data) {
            Result<?> responseEntity = (Result<?>) data;

            Map<String, String> map = new HashMap<>();

            map.put("name", "名称");
            map.put("date", LocalDate.now().format(Times.Formatter.DEFAULT_DATE));
            ExportFillData exportFillData2 = ExportFillData.builder()
                    .sheetName("Sheet1")
                    .data(map)
                    .build();

            ExportFillData exportFillData1 = ExportFillData.builder()
                    .sheetName("Sheet2")
                    .data(responseEntity.getData())
                    .build();

            return Lists.newArrayList(exportFillData1, exportFillData2);
        }
    }

    /**
     * 导出-模版-公式
     * 只支持xls格式
     */
    @GetMapping(value = "test-template-formula")
    @Export(template = "template-formula.xls", dataConvert = TemplateFormulaDataConvert.class)
    public Result<List<ExportDemoView>> testTemplateFormula() {
        return Result.success(ExportDemoView.data());
    }

    public static class TemplateFormulaDataConvert implements ExportDataConvert {
        @Override
        public List<?> convert(ExportContext exportContext, Object data) {
            Result<?> responseEntity = (Result<?>) data;
            Collection<?> list = (Collection<?>) responseEntity.getData();

            ExportFillData exportFillData1 = ExportFillData.builder()
                    .fillConfig(FillConfig.builder().forceNewRow(true).build())
                    .data(new FillWrapper("data", list))
                    .build();

            int start = 1;
            int end = start + (CollUtil.isEmpty(list) ? 0 : list.size() - 1);

            Map<String, Object> constantMap2 = new HashMap<>();
            constantMap2.put("data_end", end);

            ExportFillData exportFillData2 = ExportFillData.builder()
                    .data(constantMap2)
                    .build();

            return Lists.newArrayList(exportFillData1, exportFillData2);
        }
    }
}
