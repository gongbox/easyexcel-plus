package com.gongbo.excel.export.core.lifecycle;

import com.gongbo.excel.common.enums.ExcelType;
import com.gongbo.excel.common.result.ResultHandler;
import com.gongbo.excel.common.utils.CollectionUtil;
import com.gongbo.excel.common.utils.ResponseUtils;
import com.gongbo.excel.common.utils.StringUtil;
import com.gongbo.excel.common.utils.TemplateUtils;
import com.gongbo.excel.export.annotations.Export;
import com.gongbo.excel.export.annotations.Exports;
import com.gongbo.excel.export.config.ExportProperties;
import com.gongbo.excel.export.constants.ExportExcelType;
import com.gongbo.excel.export.core.ExportHandlers;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.exception.ExportFailedException;
import com.gongbo.excel.export.exception.NotSupportExportException;
import com.gongbo.excel.export.utils.ExportUtils;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
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
    public static Export findExportAnnotation(String exportTag, Method targetMethod) {
        Export[] annotations = Optional.ofNullable(targetMethod.getAnnotation(Exports.class))
                .map(Exports::value)
                .orElseGet(() -> targetMethod.getAnnotationsByType(Export.class));

        //没有找到注解
        if (annotations == null || annotations.length == 0) {
            throw new NotSupportExportException(MessageFormat.format(
                    "This method:{0} not support export, to enable export, please configure Export annotation on the request method to enable export!",
                    targetMethod.getName()));
        }

        //根据exportGroup过滤
        Predicate<Export> filter;
        if (StringUtil.isNotEmpty(exportTag)) {
            filter = e -> exportTag.equals(e.tag());
        } else {
            filter = e -> StringUtil.isEmpty(e.tag());
        }

        List<Export> annotationList = Arrays.stream(annotations)
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
     * @param export
     * @param exportProperties
     * @return
     */
    protected static Class<?> getModelClass(Method targetMethod, Export export, ExportProperties exportProperties, ResultHandler<?> resultHandler) {
        //没有导出模型类
        if (export.modelClass() == Export.NoneModel.class) {
            return null;
        }
        //自动模型类时且是模板导出，则没有导出模型类
        else if (export.modelClass() == Export.AutoModel.class && StringUtil.isNotEmpty(export.template())) {
            return null;
        }
        //根据方法返回类型查找
        else {
            return Optional.ofNullable(ExportUtils.getModelClass(targetMethod, exportProperties, resultHandler))
                    .orElseThrow(() -> new IllegalArgumentException("unable to get the export model class, please check the export method or add the modelClass attribute to the Export annotation!"));
        }
    }

    /**
     * 获取导出格式
     */
    protected static ExcelType getExcelType(Export export, ExportProperties exportProperties) {
        if (export.excelType() != ExportExcelType.AUTO) {
            return export.excelType().getExcelType();
        }
        if (StringUtil.isEmpty(export.template())) {
            String defaultExcelType = exportProperties.getDefaultExcelType();
            if ("xls".equalsIgnoreCase(defaultExcelType)) {
                return ExcelType.XLS;
            } else {
                return ExcelType.XLSX;
            }
        }
        return TemplateUtils.getTemplateExcelType(export.template());
    }

    /**
     * 获取文件名
     *
     * @param export
     * @return
     */
    public static String buildFileName(Export export) {
        String name = ExportHandlers.of(export.fileNameConvert())
                .apply(export.fileName());
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
        ResponseUtils.setDownloadFileHeader(serverHttpResponse, fileName + excelFileSuffix);
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
