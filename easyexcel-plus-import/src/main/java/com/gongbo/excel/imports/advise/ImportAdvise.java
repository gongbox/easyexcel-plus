package com.gongbo.excel.imports.advise;


import com.gongbo.excel.imports.core.ImportContextHolder;
import com.gongbo.excel.imports.core.ImportHandlers;
import com.gongbo.excel.imports.core.ImportHelper;
import com.gongbo.excel.imports.entity.ImportContext;
import com.gongbo.excel.imports.entity.ImportParam;
import com.gongbo.excel.imports.handler.DownloadTemplateHandler;
import com.gongbo.excel.imports.handler.ImportDataConvert;
import com.gongbo.excel.imports.handler.ReadHandler;
import com.gongbo.excel.common.result.ResultHandler;
import com.gongbo.excel.common.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Aspect
@Slf4j
public class ImportAdvise {

    @Autowired
    private ResultHandler resultHandler;

    @Pointcut("@annotation(com.gongbo.excel.imports.annotations.EnableImport)")
    public void doImport() {
    }

    @Around("doImport()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //从Http请求中获取ExportParam
        ImportParam importParam = ImportParam.parse(Objects.requireNonNull(WebUtils.getCurrentRequest()));

        if (importParam == null) {
            return joinPoint.proceed();
        }

        //获取HttpServletResponse对象
        HttpServletResponse response = Objects.requireNonNull(WebUtils.getCurrentResponse());

        //获取代理方法
        Method targetMethod = getTargetMethod(joinPoint);

        try {
            //构建导出上下文
            ImportContext importContext = ImportHelper.buildImportContext(importParam, targetMethod, resultHandler);

            try {
                ImportContextHolder.setContext(importContext);

                if (importContext.getImportParam().isTemplate()) {
                    return downloadTemplate(importContext, response);
                } else if (importContext.getImportParam().isExcel()) {
                    //执行请求，获取请求返回值
                    return doImport(importContext, WebUtils.getCurrentRequest(), joinPoint);
                } else {
                    throw new IllegalArgumentException();
                }
            } finally {
                ImportContextHolder.clear();
            }
        } catch (Exception e) {
            log.error("导出错误", e);
            throw e;
        }
    }

    private Object downloadTemplate(ImportContext importContext, HttpServletResponse response) throws IOException {
        DownloadTemplateHandler downloadTemplateHandler = ImportHandlers.of(importContext.getAutoEnableImport().downloadTemplateHandler());

        //输出下载模板
        downloadTemplateHandler.download(importContext, response);

        return null;
    }

    private Object doImport(ImportContext importContext, HttpServletRequest request, ProceedingJoinPoint joinPoint) throws Throwable {
        Part file = request.getPart(importContext.getParamName());

        if (file == null) {
            throw new IllegalArgumentException("Not found import file");
        }

        ReadHandler readHandler = ImportHandlers.of(importContext.getAutoEnableImport().readHandler());
        ImportDataConvert dataConvert = ImportHandlers.of(importContext.getAutoEnableImport().dataConvert());

        //执行读取文件
        CompletableFuture<Collection<?>> completableFuture = readHandler.read(importContext, file.getInputStream());

        //获取导入数据
        Collection<?> data = completableFuture.get(1, TimeUnit.MINUTES);

        Object[] args = joinPoint.getArgs();

        //替换对应位置参数
        args[importContext.getArgIndex()] = dataConvert.convert(importContext, data);

        return joinPoint.proceed(args);
    }

    /**
     * 获取当前请求执行方法
     */
    private static Method getTargetMethod(ProceedingJoinPoint proceedingJoinPoint) {
        return ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
    }
}