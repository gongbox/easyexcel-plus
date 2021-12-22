package com.gongbo.excel.imports.core;

import com.gongbo.excel.common.utils.WebUtils;
import com.gongbo.excel.imports.adapter.ImportAdapter;
import com.gongbo.excel.imports.adapter.ImportAdapters;
import com.gongbo.excel.imports.config.ImportProperties;
import com.gongbo.excel.imports.core.lifecycle.ImportLifecycle;
import com.gongbo.excel.imports.entity.ImportContext;
import com.gongbo.excel.imports.param.ImportParam;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor
public class ImportProxy {

    private final ImportProperties importProperties;
    private final ImportLifecycle importLifecycle;

    /**
     * 检测是否导出
     */
    public boolean isImport() {
        return importLifecycle.isImportRequest(importProperties, WebUtils.getCurrentRequest());
    }

    public Object proxy(ProceedingJoinPoint joinPoint) throws Throwable {
        Method targetMethod = getTargetMethod(joinPoint);
        HttpServletRequest httpServletRequest = Objects.requireNonNull(WebUtils.getCurrentRequest());

        //获取导出参数
        ImportParam importParam = importLifecycle.prepareParam(importProperties, httpServletRequest);

        //准备导出上下文
        ImportContext importContext = importLifecycle.prepareContext(importProperties, importParam, targetMethod);

        try {
            //设置导出上下文信息
            ImportContextHolder.setContext(importContext);
            //获取所有导出适配器
            Collection<ImportAdapter> adapters = ImportAdapters.getAdapters();
            //选择合适的适配器
            ImportAdapter importAdapter = importLifecycle.selectAdapter(importContext, adapters);
            if (importParam.isTemplate()) {
                //响应模板
                importLifecycle.responseTemplate(importContext, importAdapter);
                return null;
            } else if (importParam.isExcel()) {
                //获取导出数据
                Collection<?> list = importLifecycle.readData(importContext, importAdapter);
                //转换结果
                Object result = importLifecycle.convertData(importContext, list);
                //修改方法参数列表
                Object[] args = importLifecycle.fillArguments(importContext, joinPoint, result);
                //执行方法
                return joinPoint.proceed(args);
            } else {
                throw new UnsupportedOperationException();
            }
        } finally {
            ImportContextHolder.clear();
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
