package com.gongbo.excel.adapter.easyexcel;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.WriteContext;
import com.alibaba.excel.enums.WriteTypeEnum;
import com.alibaba.excel.exception.ExcelGenerateException;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.gongbo.excel.adapter.easyexcel.overides.ExcelWriteFillExecutor;
import com.gongbo.excel.common.enums.ExcelType;
import com.gongbo.excel.common.utils.CollectionUtil;
import com.gongbo.excel.common.utils.ReflectUtil;
import com.gongbo.excel.common.utils.StringPool;
import com.gongbo.excel.common.utils.StringUtil;
import com.gongbo.excel.export.adapter.ExportAdapter;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.entity.ExportFieldInfo;
import com.gongbo.excel.export.entity.ExportFillData;
import com.gongbo.excel.export.exception.FillKeyNotFoundException;
import com.gongbo.excel.imports.adapter.ImportAdapter;
import com.gongbo.excel.imports.entity.ImportContext;
import com.gongbo.excel.imports.utils.ImportUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class EasyExcelAdapter implements ExportAdapter, ImportAdapter {

    @Override
    public String name() {
        return "easy_excel";
    }

    private ExcelTypeEnum convert(ExcelType excelType) {
        if (excelType.getValue().equals(ExcelTypeEnum.XLS.getValue())) {
            return ExcelTypeEnum.XLS;
        } else if (excelType.getValue().equals(ExcelTypeEnum.XLSX.getValue())) {
            return ExcelTypeEnum.XLSX;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Collection<?> read(ImportContext importContext, InputStream inputStream) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        //导入数据临时存放容器
        Collection<Object> container = ImportUtils.buildCollectionContainer(importContext.getTargetArgumentContainerClass());

        CompletableFuture<Collection<?>> completableFuture = new CompletableFuture<>();

        //导入读取监听器
        ImportReadListener readListener = new ImportReadListener(container,
                (data, analysisContext) -> completableFuture.complete(data),
                (exception, analysisContext) -> completableFuture.completeExceptionally(exception));

        //导入模型类
        Class<?> modelClass = importContext.getTargetArgumentClass();

        ExcelReaderBuilder readerBuilder = EasyExcelFactory.read(inputStream, modelClass, readListener);

        ExcelReaderSheetBuilder excelReaderSheetBuilder;
        //设置读取的sheet
        if (importContext.getSheetNo() != null && importContext.getSheetNo() >= 0) {
            excelReaderSheetBuilder = readerBuilder.sheet(importContext.getSheetNo());
        } else if (StringUtil.isNotEmpty(importContext.getSheetName())) {
            excelReaderSheetBuilder = readerBuilder.sheet(importContext.getSheetName());
        } else {
            excelReaderSheetBuilder = readerBuilder.sheet(0);
        }

        //读取
        excelReaderSheetBuilder.doRead();

        //等待数据解析完成
        Integer readTimeout = importContext.getImportProperties().getReadTimeout();
        if (readTimeout == null || readTimeout <= 0) {
            return completableFuture.get();
        }
        return completableFuture.get(readTimeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public void responseTemplate(ImportContext importContext, OutputStream outputStream) throws IOException {
        //生成导入模板
        ExcelWriterBuilder excelWriterBuilder = EasyExcelFactory.write(outputStream, importContext.getTargetArgumentClass());

        excelWriterBuilder.sheet(importContext.getSheetNo(), importContext.getSheetName())
                .doWrite((Collection<?>) null);
    }

    @Override
    public void export(ExportContext exportContext, List<?> data, OutputStream outputStream) throws IOException {
        if (data == null) {
            data = Collections.emptyList();
        }
        ExcelWriterBuilder excelWriterBuilder = EasyExcelFactory.write(outputStream, exportContext.getModel());

        //需要保留的字段名
        if (CollectionUtil.isNotEmpty(exportContext.getFieldInfos())) {
            Set<String> includeColumns = exportContext.getFieldInfos().stream()
                    .map(ExportFieldInfo::getFieldName)
                    .collect(Collectors.toSet());
            excelWriterBuilder = excelWriterBuilder.includeColumnFiledNames(includeColumns);
        }

        //设置导出文件格式
        excelWriterBuilder = excelWriterBuilder.excelType(convert(exportContext.getExcelType()));

        ExcelWriterSheetBuilder excelWriterSheetBuilder = excelWriterBuilder
                .sheet(StringUtil.firstNotEmpty(exportContext.getSheetName(), exportContext.getExportProperties().getDefaultSheetName()));
        excelWriterSheetBuilder.doWrite(data);
    }

    @Override
    public void export(ExportContext exportContext, InputStream templateInputStream, List<?> data, OutputStream outputStream) {
        if (data == null) {
            data = Collections.emptyList();
        }
        ExcelWriterBuilder excelWriterBuilder = EasyExcelFactory.write(outputStream, exportContext.getModel());

        excelWriterBuilder = excelWriterBuilder.withTemplate(templateInputStream);

        //需要保留的字段名
        if (CollectionUtil.isNotEmpty(exportContext.getFieldInfos())) {
            Set<String> includeColumns = exportContext.getFieldInfos().stream()
                    .map(ExportFieldInfo::getFieldName)
                    .collect(Collectors.toSet());
            excelWriterBuilder = excelWriterBuilder.includeColumnFiledNames(includeColumns);
        }

        //设置导出文件格式
        excelWriterBuilder = excelWriterBuilder.excelType(convert(exportContext.getExcelType()));

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
        ExcelWriteFillExecutor excelWriteFillExecutor = new ExcelWriteFillExecutor(excelWriter.writeContext());

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
                    fill(excelWriter.writeContext(), excelWriteFillExecutor, exportFillData.getData(), (FillConfig) exportFillData.getFillConfig(), writeSheet, true);
                }
            } else {
                WriteSheet writeSheet = new ExcelWriterSheetBuilder(excelWriter)
                        .sheetNo(exportFillData.getSheetNo())
                        .sheetName(exportFillData.getSheetName())
                        .build();
                //填充
                fill(excelWriter.writeContext(), excelWriteFillExecutor, exportFillData.getData(), (FillConfig) exportFillData.getFillConfig(), writeSheet, false);
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

    public void fill(WriteContext context, ExcelWriteFillExecutor excelWriteFillExecutor,
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

    @Override
    public List<ExportFieldInfo> findExportFieldInfos(Class<?> clazz) {
        return ReflectUtil.getFields(clazz, true).stream()
                .map(this::findExportFieldInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

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
}
