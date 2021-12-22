package com.gongbo.excel.export.core.lifecycle;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.gongbo.excel.common.result.ResultHandler;
import com.gongbo.excel.common.utils.CollectionUtil;
import com.gongbo.excel.common.utils.StringUtil;
import com.gongbo.excel.export.annotations.ExcelExport;
import com.gongbo.excel.export.annotations.ExcelExports;
import com.gongbo.excel.export.config.ExportProperties;
import com.gongbo.excel.export.constants.ExcelType;
import com.gongbo.excel.export.core.ExportHandlers;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.exception.ExportFailedException;
import com.gongbo.excel.export.exception.NotSupportExportException;
import com.gongbo.excel.export.utils.ExportUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractExportLifeCycle implements ExportLifecycle {

    private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HTTP_HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String BEFORE_EXPORT_RESPONSE_HEADER = "BEFORE_EXPORT_RESPONSE_HEADER";

    /**
     * 根据exportTag获取对应Export注解
     *
     * @param exportTag
     * @param targetMethod
     * @return
     */
    public static ExcelExport findExportAnnotation(String exportTag, Method targetMethod) {
        ExcelExport[] annotations = Optional.ofNullable(targetMethod.getAnnotation(ExcelExports.class))
                .map(ExcelExports::value)
                .orElseGet(() -> targetMethod.getAnnotationsByType(ExcelExport.class));

        //没有找到注解
        if (annotations == null || annotations.length == 0) {
            throw new NotSupportExportException(MessageFormat.format(
                    "This method:{0} not support export, to enable export, please configure ExcelExport annotation on the request method to enable export!",
                    targetMethod.getName()));
        }

        //根据exportGroup过滤
        Predicate<ExcelExport> filter;
        if (StringUtil.isNotEmpty(exportTag)) {
            filter = e -> exportTag.equals(e.tag());
        } else {
            filter = e -> StringUtil.isEmpty(e.tag());
        }

        List<ExcelExport> annotationList = Arrays.stream(annotations)
                .filter(filter)
                .collect(Collectors.toList());

        //没有找到对应注解
        if (CollectionUtil.isEmpty(annotationList)) {
            throw new NotSupportExportException(MessageFormat.format("no matching export tag[{0}] on this method[{1}]", exportTag, targetMethod.getName()));
        }

        //多个注解匹配
        if (annotationList.size() > 1) {
            throw new ExportFailedException(MessageFormat.format("more than one export tag[{0}] matched on this method [{1}]", exportTag, targetMethod.getName()));
        }

        //返回匹配的注解
        return annotationList.get(0);
    }

    /**
     * 获取导出模型类
     *
     * @param targetMethod
     * @param excelExport
     * @param exportProperties
     * @return
     */
    protected static Class<?> getModelClass(Method targetMethod, ExcelExport excelExport, ExportProperties exportProperties, ResultHandler<?> resultHandler) {
        //没有导出模型类
        if (excelExport.modelClass() == ExcelExport.NoneModel.class) {
            return null;
        }
        //自动模型类时且是模板导出，则没有导出模型类
        else if (excelExport.modelClass() == ExcelExport.AutoModel.class && StringUtil.isNotEmpty(excelExport.template())) {
            return null;
        }
        //根据方法返回类型查找
        else {
            return Optional.ofNullable(ExportUtils.getModelClass(targetMethod, exportProperties, resultHandler))
                    .orElseThrow(() -> new IllegalArgumentException("unable to get the export model class, please check the export method or add the modelClass attribute to the ExcelExport annotation!"));
        }
    }

    /**
     * 获取导出格式
     */
    protected static ExcelTypeEnum getExcelType(ExcelExport excelExport, ExportProperties exportProperties) {
        if (excelExport.excelType() != ExcelType.AUTO) {
            return excelExport.excelType().getExcelTypeEnum();
        }
        if (StringUtil.isEmpty(excelExport.template())) {
            String defaultExcelType = exportProperties.getDefaultExcelType();
            if ("xls".equalsIgnoreCase(defaultExcelType)) {
                return ExcelTypeEnum.XLS;
            } else {
                return ExcelTypeEnum.XLSX;
            }
        }
        if (excelExport.template().endsWith(ExcelTypeEnum.XLSX.getValue())) {
            return ExcelTypeEnum.XLSX;
        } else if (excelExport.template().endsWith(ExcelTypeEnum.XLS.getValue())) {
            return ExcelTypeEnum.XLS;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 获取文件名
     *
     * @param excelExport
     * @return
     */
    public static String buildFileName(ExcelExport excelExport) {
        String name = ExportHandlers.of(excelExport.fileNameConvert())
                .apply(excelExport.fileName());
        if (StringUtil.isEmpty(name)) {
            name = String.valueOf(System.currentTimeMillis());
        }
        return name;
    }

    /**
     * 设置响应头信息
     *
     * @param serverHttpResponse
     * @param exportContext
     */
    public static void setDownloadResponseHeaders(HttpServletResponse serverHttpResponse, ExportContext exportContext) {
        String fileName = exportContext.getFileName();
        String excelFileSuffix = exportContext.getExcelType().getValue();
        setDownloadResponseHeaders(serverHttpResponse, fileName + excelFileSuffix);
    }

    /**
     * 设置响应头信息
     *
     * @param serverHttpResponse
     * @param fileName
     */
    public static void setDownloadResponseHeaders(HttpServletResponse serverHttpResponse, String fileName) {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        serverHttpResponse.addHeader(HTTP_HEADER_CONTENT_TYPE, "application/vnd.ms-excel;charset=UTF-8");
        try {
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException ignored) {
        }
        serverHttpResponse.addHeader(HTTP_HEADER_CONTENT_DISPOSITION, "attachment;filename*=utf-8''" + fileName);
    }

    /**
     * 获取默认的请求头信息
     *
     * @param serverHttpResponse
     * @return
     */
    public static void storeResponseHeaders(ExportContext exportContext, HttpServletResponse serverHttpResponse) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HTTP_HEADER_CONTENT_TYPE, serverHttpResponse.getHeader(HTTP_HEADER_CONTENT_TYPE));
        if (serverHttpResponse.containsHeader(HTTP_HEADER_CONTENT_DISPOSITION)) {
            headers.put(HTTP_HEADER_CONTENT_DISPOSITION, serverHttpResponse.getHeader(HTTP_HEADER_CONTENT_DISPOSITION));
        }
        exportContext.getUserContext().put(BEFORE_EXPORT_RESPONSE_HEADER, headers);
    }

    /**
     * @param serverHttpResponse
     */
    public static void resetResponseHeaders(ExportContext exportContext, HttpServletResponse serverHttpResponse) {
        if (!exportContext.getUserContext().containsKey(BEFORE_EXPORT_RESPONSE_HEADER)) {
            return;
        }
        Map<String, String> responseHeader = (Map<String, String>) exportContext.getUserContext().get(BEFORE_EXPORT_RESPONSE_HEADER);
        serverHttpResponse.setHeader(HTTP_HEADER_CONTENT_TYPE, responseHeader.get(HTTP_HEADER_CONTENT_TYPE));
        serverHttpResponse.setHeader(HTTP_HEADER_CONTENT_DISPOSITION, responseHeader.getOrDefault(HTTP_HEADER_CONTENT_DISPOSITION, null));
        exportContext.getUserContext().remove(BEFORE_EXPORT_RESPONSE_HEADER);
    }
}
