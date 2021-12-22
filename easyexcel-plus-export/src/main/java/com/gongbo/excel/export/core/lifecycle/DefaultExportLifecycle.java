package com.gongbo.excel.export.core.lifecycle;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.gongbo.excel.common.utils.StringUtil;
import com.gongbo.excel.common.utils.Utils;
import com.gongbo.excel.common.utils.WebUtils;
import com.gongbo.excel.export.adapter.ExportAdapter;
import com.gongbo.excel.export.annotations.ExcelExport;
import com.gongbo.excel.export.config.ExportProperties;
import com.gongbo.excel.export.core.ExportHandlers;
import com.gongbo.excel.export.custom.ExportDataConvert;
import com.gongbo.excel.export.custom.FieldFilter;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.entity.ExportFieldInfo;
import com.gongbo.excel.export.param.ExportParam;
import com.gongbo.excel.export.utils.ExportUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DefaultExportLifecycle extends AbstractExportLifeCycle implements ExportLifecycle {

    @Override
    public boolean isExportRequest(ExportProperties exportProperties, HttpServletRequest request) {
        String export = Utils.firstNotEmpty(() -> request.getParameter(ExportParam.EXPORT),
                () -> request.getHeader(ExportParam.EXPORT));
        return StringUtil.isNotEmpty(export);
    }

    @Override
    public ExportParam prepareParam(ExportProperties exportProperties, HttpServletRequest request) {
        String export = Utils.firstNotEmpty(() -> request.getParameter(ExportParam.EXPORT),
                () -> request.getHeader(ExportParam.EXPORT));

        if (StringUtil.isEmpty(export)) {
            return null;
        }

        ExportParam.Type type = ExportParam.Type.of(export);

        Objects.requireNonNull(type, MessageFormat.format("没有找到对应的导出类型:{0}", export));

        //获取exportTag参数
        String exportTag = Utils.firstNotEmpty(() -> request.getParameter(ExportParam.EXPORT_TAG),
                () -> request.getHeader(ExportParam.EXPORT_TAG));

        return ExportParam.builder()
                .exportTag(exportTag)
                .type(type)
                .build();
    }

    @Override
    public ExportContext prepareContext(ExportProperties exportProperties, ExportParam exportParam, Method targetMethod) {
        //查找对应ExcelExport注解
        ExcelExport excelExport = findExportAnnotation(exportParam.getExportTag(), targetMethod);

        //获取对应模型类
        Class<?> modelClass = getModelClass(targetMethod, excelExport, exportProperties, exportProperties.getResultHandler());

        //获取导出文件名
        String fileName = buildFileName(excelExport);

        //导出文件格式
        ExcelTypeEnum excelType = getExcelType(excelExport, exportProperties);

        //获取输出路径
        String outputPath = excelExport.outputPath();

        return ExportContext.builder()
                .exportProperties(exportProperties)
                .resultHandler(exportProperties.getResultHandler())
                .modelClass(modelClass)
                .excelExport(excelExport)
                .fileName(fileName)
                .sheetName(excelExport.sheetName())
                .template(excelExport.template())
                .excelType(excelType)
                .fieldInfos(null)
                .exportParam(exportParam)
                .outputPath(outputPath)
                .formula(excelExport.formula())
                .responseResult(excelExport.responseResult())
                .userContext(new HashMap<>())
                .build();
    }

    @Override
    public ExportAdapter selectAdapter(ExportContext exportContext, Collection<ExportAdapter> adapters) {
        String exportBy = exportContext.getExcelExport().exportBy();

        if (StringUtil.isEmpty(exportBy)) {
            //取默认导出
            exportBy = exportContext.getExportProperties().getDefaultExportBy();
        }

        for (ExportAdapter adapter : adapters) {
            if (exportBy.equals(adapter.name())) {
                return adapter;
            }
        }
        throw new IllegalArgumentException(MessageFormat.format("没有找到名为{0}的适配器", exportBy));
    }

    @Override
    public void prepareExportFieldInfos(ExportContext exportContext, ExportAdapter exportAdapter) {
        Class<?> modelClass = exportContext.getModelClass();
        List<ExportFieldInfo> fieldInfos;
        if (modelClass == null) {
            fieldInfos = Collections.emptyList();
        } else {
            List<ExportFieldInfo> exportFieldInfos = exportAdapter.findExportFieldInfos(exportContext.getModelClass());
            //字段过滤器
            FieldFilter fieldFilter = ExportHandlers.of(exportContext.getExcelExport().fieldFilter());

            fieldInfos = exportFieldInfos
                    .stream()
                    .filter(fieldFilter::predict)
                    .collect(Collectors.toList());
        }

        exportContext.setFieldInfos(fieldInfos);
    }

    @Override
    public List<?> prepareData(ExportContext exportContext, Object result) {
        ExportDataConvert exportDataConvert = ExportHandlers.of(exportContext.getExcelExport().dataConvert());
        return exportDataConvert.convert(exportContext, result);
    }

    @Override
    public OutputStream prepareOutputStream(ExportContext exportContext) throws IOException {
        if (exportContext.isOutputFile()) {
            Files.createDirectories(Paths.get(exportContext.getOutputPath()));
            File file = new File(exportContext.getOutputPath(), exportContext.getFileName() + exportContext.getExcelType().getValue());
            return new FileOutputStream(file);
        } else {
            HttpServletResponse response = WebUtils.getCurrentResponse();
            //保存当前响应头信息
            storeResponseHeaders(exportContext, response);
            //设置响应头信息
            setDownloadResponseHeaders(response, exportContext);
            return response.getOutputStream();
        }
    }

    @Override
    public void export(ExportContext exportContext, List<?> data, ExportAdapter adapter, OutputStream outputStream) throws IOException {
        String template = exportContext.getExcelExport().template();
        if (StringUtil.isNotEmpty(template)) {
            InputStream templateInputStream = ExportUtils.getTemplateInputStream(exportContext);
            //模板导出
            adapter.export(exportContext, templateInputStream, data, outputStream);
        } else {
            //普通导出
            adapter.export(exportContext, data, outputStream);
        }
    }

    @Override
    public void reset(ExportContext exportContext) {
        //清除响应头信息
        resetResponseHeaders(exportContext, WebUtils.getCurrentResponse());
    }
}
