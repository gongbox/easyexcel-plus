package com.gongbo.excel.export.core.provider.easyexcel;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.WriteContext;
import com.alibaba.excel.enums.WriteTypeEnum;
import com.alibaba.excel.exception.ExcelGenerateException;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.gongbo.excel.export.core.provider.ExportProvider;
import com.gongbo.excel.export.core.provider.easyexcel.overrides.MyExcelWriteFillExecutor;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.entity.ExportFieldInfo;
import com.gongbo.excel.export.entity.fill.ExportFillData;
import com.gongbo.excel.export.exception.FillKeyNotFoundException;
import com.gongbo.excel.common.utils.CollectionUtil;
import com.gongbo.excel.common.utils.StringPool;
import com.gongbo.excel.common.utils.StringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EasyExcelProvider implements ExportProvider {

    private static final EasyExcelProvider INSTANCE = new EasyExcelProvider();

    public static EasyExcelProvider getInstance() {
        return INSTANCE;
    }

    /**
     * @param field
     * @return
     */
    @Override
    public ExportFieldInfo findExportFieldInfo(Field field) {
        ExcelProperty exportField = field.getAnnotation(ExcelProperty.class);
        if (exportField != null) {
            String name = null;
            String[] value = exportField.value();
            //取最后一个作为字段名
            if (value.length > 0) {
                name = String.join(".", value);
            }

            return ExportFieldInfo.builder()
                    .fieldName(field.getName())
                    .name(name)
                    .order(exportField.index())
                    .build();
        }
        return null;
    }

    @Override
    public void export(ExportContext exportContext, List<?> data, OutputStream outputStream) throws IOException {
        if (data == null) {
            data = Collections.emptyList();
        }

        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(outputStream, exportContext.getModelClass());

        boolean fillTemplate = StringUtil.isNotEmpty(exportContext.getTemplate());
        if (fillTemplate) {
            String templatePath = exportContext.getExportProperties().getTemplatePath();
            InputStream inputStream;
            if (templatePath.startsWith("classpath:")) {
                ClassPathResource resource = new ClassPathResource(templatePath.replaceFirst("classpath:", "") + exportContext.getTemplate());
                inputStream = resource.getInputStream();
            } else {
                inputStream = Files.newInputStream(Paths.get(templatePath));
            }
            excelWriterBuilder = excelWriterBuilder.withTemplate(inputStream);
        }

        //需要保留的字段名
        if (CollectionUtil.isNotEmpty(exportContext.getFieldInfos())) {
            Set<String> includeColumns = exportContext.getFieldInfos().stream()
                    .map(ExportFieldInfo::getFieldName)
                    .collect(Collectors.toSet());
            excelWriterBuilder = excelWriterBuilder.includeColumnFiledNames(includeColumns);
        }

        //设置导出文件格式
        excelWriterBuilder = excelWriterBuilder.excelType(exportContext.getExcelType());

        if (fillTemplate) {
            //模板导出
            exportWithTemplate(exportContext, data, excelWriterBuilder);
        } else {
            //普通导出
            export(exportContext, data, excelWriterBuilder);
        }
    }


    /**
     * 导出
     */
    protected void export(ExportContext exportContext, List<?> data, ExcelWriterBuilder excelWriterBuilder) {
        ExcelWriterSheetBuilder excelWriterSheetBuilder = excelWriterBuilder
                .sheet(StringUtil.firstNotEmpty(exportContext.getSheetName(), exportContext.getExportProperties().getDefaultSheetName()));
        excelWriterSheetBuilder.doWrite(data);
    }

    /**
     * 模板导出
     */
    protected void exportWithTemplate(ExportContext exportContext, List<?> data, ExcelWriterBuilder excelWriterBuilder) {
        ExcelWriter excelWriter = excelWriterBuilder
                .build();

        //获取填充数据
        List<ExportFillData> exportFillDataList = exportContext.listExportFillData();

        //data是否是填充数据标志（判断依据：data不为空且data中没有FillEntity类型数据）
        boolean dataFillFlag;

        //合并数据与填充数据
        if (dataFillFlag = CollectionUtil.isNotEmpty(data)) {
            for (Object item : data) {
                if (item instanceof ExportFillData) {
                    exportFillDataList.add((ExportFillData) item);
                    dataFillFlag = false;
                }
            }
        }

        //使用自己的执行器
        MyExcelWriteFillExecutor excelWriteFillExecutor = new MyExcelWriteFillExecutor(excelWriter.writeContext());

        //data是填充数据
        if (dataFillFlag) {
            WriteSheet writeSheet = new ExcelWriterSheetBuilder(excelWriter)
                    .sheetName(StringUtil.firstNotEmpty(exportContext.getSheetName(), exportContext.getExportProperties().getDefaultSheetName()))
                    .build();

            //填充
            fill(excelWriter.writeContext(), excelWriteFillExecutor, data, null, writeSheet, false);
        }

        //填充数据
        for (ExportFillData exportFillData : exportFillDataList) {
            if (exportFillData.isFillAllSheet()) {
                //获取所有sheet个数
                int numberOfSheets = excelWriter.writeContext()
                        .writeWorkbookHolder()
                        .getWorkbook()
                        .getNumberOfSheets();

                //遍历所有sheet填充
                for (int sheetNo = 0; sheetNo < numberOfSheets; sheetNo++) {
                    WriteSheet writeSheet = new ExcelWriterSheetBuilder(excelWriter)
                            .sheetNo(sheetNo)
                            .build();

                    //填充
                    fill(excelWriter.writeContext(), excelWriteFillExecutor, exportFillData.getData(), exportFillData.getFillConfig(), writeSheet, true);
                }
            } else {
                WriteSheet writeSheet = new ExcelWriterSheetBuilder(excelWriter)
                        .sheetNo(exportFillData.getSheetNo())
                        .sheetName(exportFillData.getSheetName())
                        .build();
                //填充
                fill(excelWriter.writeContext(), excelWriteFillExecutor, exportFillData.getData(), exportFillData.getFillConfig(), writeSheet, false);
            }
        }

        //公式填充
        if (exportContext.isFormula()) {
            doFormula(exportContext, excelWriter.writeContext().writeWorkbookHolder().getWorkbook());
        }

        excelWriter.finish();
    }

    //公式填充
    private void doFormula(ExportContext exportContext, Workbook workbook) {
        workbook.setForceFormulaRecalculation(true);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            for (Row r : sheet) {
                for (Cell c : r) {
                    if (c != null && c.getCellTypeEnum() == CellType.STRING) {
                        String cell = c.getStringCellValue();
                        if (StringUtil.isNotEmpty(cell)) {
                            if (cell.startsWith(StringPool.EQUALS)) {
                                c.setCellFormula(cell.substring(1));
                                evaluator.evaluate(c);
                            } else if (cell.startsWith(exportContext.getExportProperties().getFormulaPrefix())) {
                                c.setCellFormula(cell.substring(exportContext.getExportProperties().getFormulaPrefix().length()));
                                evaluator.evaluate(c);
                            }
                        }
                    }
                }
            }
        }
    }

    public void fill(WriteContext context, MyExcelWriteFillExecutor excelWriteFillExecutor,
                     Object data, FillConfig fillConfig, WriteSheet writeSheet,
                     boolean ignoreFillKeyNotFound) {
        try {
            if (context.writeWorkbookHolder().getTempTemplateInputStream() == null) {
                throw new ExcelGenerateException("Calling the 'fill' method must use a template.");
            }
            context.currentSheet(writeSheet, WriteTypeEnum.FILL);

            excelWriteFillExecutor.fill(data, fillConfig);
        } catch (FillKeyNotFoundException e) {
            if (!ignoreFillKeyNotFound) {
                context.finish(true);
                throw e;
            }
        } catch (RuntimeException e) {
            context.finish(true);
            throw e;
        } catch (Exception e) {
            context.finish(true);
            throw new ExcelGenerateException(e);
        }
    }
}
