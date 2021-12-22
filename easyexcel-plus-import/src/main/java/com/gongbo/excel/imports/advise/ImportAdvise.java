package com.gongbo.excel.imports.advise;


import com.gongbo.excel.imports.config.ImportProperties;
import com.gongbo.excel.imports.core.ImportProxy;
import com.gongbo.excel.imports.core.lifecycle.DefaultImportLifecycle;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
@Slf4j
public class ImportAdvise {

    @Autowired
    private ImportProperties importProperties;

    @Pointcut("@annotation(com.gongbo.excel.imports.annotations.ExcelImport)")
    public void doImport() {
    }

    @Around("doImport()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ImportProxy importProxy = new ImportProxy(importProperties, new DefaultImportLifecycle());
        //判断是否是导出
        if (importProxy.isImport()) {
            try {
                //执行导出
                return importProxy.proxy(joinPoint);
            } catch (Throwable e) {
                log.error("import error", e);
                throw e;
            }
        }
        return joinPoint.proceed();
    }
}