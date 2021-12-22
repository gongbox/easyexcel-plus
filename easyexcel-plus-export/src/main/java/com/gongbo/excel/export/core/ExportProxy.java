package com.gongbo.excel.export.core;


import com.gongbo.excel.common.utils.WebUtils;
import com.gongbo.excel.export.adapter.ExportAdapter;
import com.gongbo.excel.export.adapter.ExportAdapters;
import com.gongbo.excel.export.config.ExportProperties;
import com.gongbo.excel.export.core.lifecycle.ExportLifecycle;
import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.param.ExportParam;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ExportProxy {

    private final ExportProperties exportProperties;
    private final ExportLifecycle exportLifecycle;

    /**
     * 检测是否导出
     */
    public boolean isExport() {
        return exportLifecycle.isExportRequest(exportProperties, WebUtils.getCurrentRequest());
    }

    /**
     * 执行导出
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    public Object export(ProceedingJoinPoint joinPoint) throws Throwable {
        Method targetMethod = getTargetMethod(joinPoint);
        HttpServletRequest httpServletRequest = Objects.requireNonNull(WebUtils.getCurrentRequest());

        //获取导出参数
        ExportParam exportParam = exportLifecycle.prepareParam(exportProperties, httpServletRequest);

        //准备导出上下文
        ExportContext exportContext = exportLifecycle.prepareContext(exportProperties, exportParam, targetMethod);

        try {
            //设置导出上下文信息
            ExportContextHolder.setContext(exportContext);

            //获取所有导出适配器
            Collection<ExportAdapter> adapters = ExportAdapters.getAdapters();

            //选择合适的适配器
            ExportAdapter exportAdapter = exportLifecycle.selectAdapter(exportContext, adapters);

            //生成导出字段信息
            exportLifecycle.prepareExportFieldInfos(exportContext, exportAdapter);

            //执行
            Object result = joinPoint.proceed();

            //获取导出数据
            List<?> list = exportLifecycle.prepareData(exportContext, result);

            //准备输出流
            OutputStream outputStream = exportLifecycle.prepareOutputStream(exportContext);

            //输出
            exportLifecycle.export(exportContext, list, exportAdapter, outputStream);

            //恢复
            exportLifecycle.reset(exportContext);

            //响应结果
            if (exportContext.isResponseResult()) {
                return result;
            } else {
                return null;
            }
        } finally {
            ExportContextHolder.clear();
        }
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
