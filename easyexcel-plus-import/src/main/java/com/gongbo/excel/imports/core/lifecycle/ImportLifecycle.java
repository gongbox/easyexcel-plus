package com.gongbo.excel.imports.core.lifecycle;

import com.gongbo.excel.imports.adapter.ImportAdapter;
import com.gongbo.excel.imports.config.ImportProperties;
import com.gongbo.excel.imports.entity.ImportContext;
import com.gongbo.excel.imports.param.ImportParam;
import org.aspectj.lang.ProceedingJoinPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ImportLifecycle {

    /**
     * 当前请求是否是导入请求
     *
     * @return
     */
    boolean isImportRequest(ImportProperties importProperties, HttpServletRequest request);

    /**
     * 导入参数提取
     *
     * @return 导入参数
     */
    ImportParam prepareParam(ImportProperties importProperties, HttpServletRequest request);

    /**
     * 构建导出上下文
     *
     * @param importParam
     * @param targetMethod
     * @return 导出上下文
     */
    ImportContext prepareContext(ImportProperties importProperties, ImportParam importParam, Method targetMethod);

    /**
     * ImportAdapter选择
     *
     * @param importContext
     * @param adapters
     * @return
     */
    ImportAdapter selectAdapter(ImportContext importContext, Collection<ImportAdapter> adapters);

    /**
     * 响应模板文件
     */
    void responseTemplate(ImportContext importContext, ImportAdapter importAdapter) throws IOException;

    /**
     * 读取数据
     *
     * @param importContext
     * @param importAdapter
     * @return
     */
    Collection<?> readData(ImportContext importContext, ImportAdapter importAdapter) throws IOException, ExecutionException, InterruptedException, TimeoutException, ServletException;

    /**
     * 数据转换
     *
     * @param importContext
     * @param data
     * @return
     */
    Object convertData(ImportContext importContext, Collection<?> data);

    /**
     * 填充请求参数
     *
     * @param importContext
     * @param joinPoint
     * @param data
     * @return
     */
    Object[] fillArguments(ImportContext importContext, ProceedingJoinPoint joinPoint, Object data);

}
