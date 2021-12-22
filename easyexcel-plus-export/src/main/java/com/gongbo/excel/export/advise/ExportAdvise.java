package com.gongbo.excel.export.advise;


import com.gongbo.excel.export.config.ExportProperties;
import com.gongbo.excel.export.core.lifecycle.DefaultExportLifecycle;
import com.gongbo.excel.export.core.ExportProxy;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
@Slf4j
public class ExportAdvise {

    @Autowired
    private ExportProperties exportProperties;

    @Pointcut("@annotation(com.gongbo.excel.export.annotations.ExcelExport) || @annotation(com.gongbo.excel.export.annotations.ExcelExports)")
    public void doExport() {
    }

    /**
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("doExport()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ExportProxy exportProxy = new ExportProxy(exportProperties, new DefaultExportLifecycle());
        //判断是否是导出
        if (exportProxy.isExport()) {
            try {
                //执行导出
                return exportProxy.export(joinPoint);
            } catch (Throwable e) {
                log.error("export error", e);
                throw e;
            }
        }
        return joinPoint.proceed();
    }

}
