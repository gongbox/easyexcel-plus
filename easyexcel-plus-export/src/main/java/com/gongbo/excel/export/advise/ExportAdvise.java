package com.gongbo.excel.export.advise;


import com.gongbo.excel.export.config.ExportProperties;
import com.gongbo.excel.export.core.ExportContextHolder;
import com.gongbo.excel.export.core.ExportHandlers;
import com.gongbo.excel.export.core.ExportHelper;
import com.gongbo.excel.export.core.handler.ExportDataConvert;
import com.gongbo.excel.export.core.handler.ExportLifecycle;
import com.gongbo.excel.export.core.handler.WriteHandler;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.entity.ExportInfo;
import com.gongbo.excel.export.entity.ExportParam;
import com.gongbo.excel.export.exception.NotSupportExportException;
import com.gongbo.excel.export.utils.ExportUtils;
import com.gongbo.excel.common.result.ResultHandler;
import com.gongbo.excel.common.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

@Aspect
@Slf4j
public class ExportAdvise {

    @Autowired
    private ResultHandler resultHandler;

    @Autowired
    private ExportProperties exportProperties;

    @Pointcut("@annotation(com.gongbo.excel.export.annotations.EnableExport) || @annotation(com.gongbo.excel.export.annotations.EnableExports)")
    public void doExport() {
    }

    /**
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("doExport()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //从Http请求中获取ExportParam
        ExportParam exportParam = ExportParam.parse(Objects.requireNonNull(WebUtils.getCurrentRequest()));

        if (exportParam == null) {
            return joinPoint.proceed();
        }
        //获取HttpServletResponse对象
        HttpServletResponse response = Objects.requireNonNull(WebUtils.getCurrentResponse());

        //获取代理方法
        Method targetMethod = getTargetMethod(joinPoint);

        try {
            //执行导出
            return doExport(exportParam, joinPoint, targetMethod, response);
        } catch (Exception e) {
            log.error("export error", e);
            throw e;
        }
    }

    /**
     * 执行导出
     *
     * @param exportParam
     * @param joinPoint
     * @param targetMethod
     * @param response
     * @return
     * @throws Throwable
     */
    private Object doExport(ExportParam exportParam, ProceedingJoinPoint joinPoint, Method targetMethod, HttpServletResponse response) throws Throwable {
        //构建导出上下文
        ExportContext exportContext = ExportHelper.buildExportContext(exportParam, targetMethod, exportProperties, resultHandler);

        try {
            ExportContextHolder.setContext(exportContext);

            if (exportContext.getExportParam().isInfo()) {
                return resultHandler.success(ExportInfo.buildExportInfo(exportContext));
            } else if (exportContext.getExportParam().isExcel()) {
                //执行请求，获取请求返回值
                Object result = joinPoint.proceed();
                if (!(resultHandler.check(result))) {
                    throw new NotSupportExportException(MessageFormat.format("request api return type must be class:{0}", exportProperties.getResponseClassName()));
                } else {
                    return responseExcel(exportContext, result, response);
                }
            } else {
                throw new IllegalArgumentException();
            }
        } finally {
            ExportContextHolder.clear();
        }
    }

    /**
     * 响应文件
     *
     * @param exportContext
     * @param result
     * @param response
     * @return
     * @throws IOException
     */
    private static Object responseExcel(ExportContext exportContext, Object result, HttpServletResponse response) throws IOException {
        ExportLifecycle exportLifecycle = ExportHandlers.of(exportContext.getEnableExport().afterExportHandler());

        //
        exportLifecycle.afterPrepared(exportContext);

        //数据转换
        ExportDataConvert exportDataConvert = ExportHandlers.of(exportContext.getEnableExport().dataConvert());
        List<?> data = exportDataConvert.convert(exportContext, result);

        //
        exportLifecycle.afterDataConverted(exportContext, data);

        //执行导出
        WriteHandler writeHandler = ExportHandlers.of(exportContext.getEnableExport().writeHandler());
        //导出
        writeHandler.write(exportContext, data, ExportUtils.getExportOutputStream(exportContext, response));

        //导出文件后执行
        exportLifecycle.afterExport(exportContext);

        return exportContext.isResponseResult() ? result : null;
    }

    /**
     * 获取当前请求执行方法
     *
     * @param proceedingJoinPoint
     * @return
     */
    private static Method getTargetMethod(ProceedingJoinPoint proceedingJoinPoint) {
        return ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
    }

}
