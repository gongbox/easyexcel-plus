package com.gongbo.excel.imports.core.lifecycle;

import com.gongbo.excel.common.enums.ExcelType;
import com.gongbo.excel.common.utils.*;
import com.gongbo.excel.imports.adapter.ImportAdapter;
import com.gongbo.excel.imports.annotations.Import;
import com.gongbo.excel.imports.config.ImportProperties;
import com.gongbo.excel.imports.entity.ImportContext;
import com.gongbo.excel.imports.exception.NotSupportImportException;
import com.gongbo.excel.imports.param.ImportParam;
import com.gongbo.excel.imports.utils.ImportUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.FileCopyUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.gongbo.excel.imports.param.ImportParam.IMPORT;


@Slf4j
public class DefaultImportLifecycle implements ImportLifecycle {
    /**
     * 当前请求是否是导入请求
     *
     * @return true:是，false：否
     */
    @Override
    public boolean isImportRequest(ImportProperties importProperties, HttpServletRequest request) {
        return StringUtil.isNotEmpty(Utils.firstNotEmpty(() -> request.getParameter(IMPORT), () -> request.getHeader(IMPORT)));
    }

    /**
     * 导入参数提取
     *
     * @return 导入参数
     */
    @Override
    public ImportParam prepareParam(ImportProperties importProperties, HttpServletRequest request) {
        String param = Utils.firstNotEmpty(() -> request.getParameter(IMPORT), () -> request.getHeader(IMPORT));

        Objects.requireNonNull(param);

        ImportParam.Type type = ImportParam.Type.of(param);

        Objects.requireNonNull(type);

        return ImportParam.builder()
                .type(type)
                .build();
    }

    /**
     * 构建导出上下文
     *
     * @param importParam
     * @param targetMethod
     * @return 导出上下文
     */
    @Override
    public ImportContext prepareContext(ImportProperties importProperties, ImportParam importParam, Method targetMethod) {
        Import importAnnotation = targetMethod.getAnnotation(Import.class);
        //检查是否支持导入
        if (importAnnotation == null) {
            throw new NotSupportImportException(MessageFormat.format("this method:{0} not support import, to enable import, please configure Import annotation on the request method to enable import", targetMethod.getName()));
        }

        //目标参数位置
        boolean mustExists = !importParam.isTemplate() || importAnnotation.model() == Object.class;
        Integer argIndex = ImportUtils.getImportTargetArgIndex(targetMethod, mustExists);
        //目标参数类型
        Class<?> modelContainerClass = argIndex == null ? null : ImportUtils.getModelContainerClass(targetMethod, argIndex);
        //导入数据模型类
        Class<?> modelClass;
        if (importAnnotation.model() != Object.class) {
            modelClass = importAnnotation.model();
        } else {
            modelClass = ImportUtils.getModelClass(targetMethod, argIndex);

            if (modelClass == null) {
                throw new IllegalArgumentException("unable to get the import model class, please check the import method or add the modelClass attribute to the Import annotation!");
            }
        }

        String sheetName = importAnnotation.sheetName();

        return ImportContext.builder()
                .importProperties(importProperties)
                .template(importAnnotation.template())
                .templateFilename(importAnnotation.templateFilename())
                .importParam(importParam)
                .anImport(importAnnotation)
                .fileParam(importAnnotation.fileParam())
                .sheetNo(importAnnotation.sheetNo() >= 0 ? importAnnotation.sheetNo() : null)
                .sheetName(sheetName)
                .targetArgumentIndex(argIndex)
                .targetArgumentContainerClass(modelContainerClass)
                .targetArgumentClass(modelClass)
                .build();
    }

    /**
     * ImportAdapter选择
     *
     * @param importContext
     * @param adapters
     * @return
     */
    @Override
    public ImportAdapter selectAdapter(ImportContext importContext, Collection<ImportAdapter> adapters) {
        String importBy = importContext.getAnImport().importBy();

        if (StringUtil.isEmpty(importBy)) {
            //取默认导出
            importBy = importContext.getImportProperties().getDefaultImportBy();
        }

        for (ImportAdapter adapter : adapters) {
            if (importBy.equals(adapter.name())) {
                return adapter;
            }
        }
        throw new IllegalArgumentException(MessageFormat.format("没有找到名为{0}的适配器", importBy));
    }

    /**
     * 响应模板文件
     */
    @Override
    public void responseTemplate(ImportContext importContext, ImportAdapter importAdapter) throws IOException {
        HttpServletResponse response = WebUtils.getCurrentResponse();

        String responseFilename = importContext.getTemplateFilename();
        if (StringUtil.isEmpty(responseFilename)) {
            responseFilename = String.valueOf(System.currentTimeMillis());
        }

        if (StringUtil.isNotEmpty(importContext.getTemplate())) {
            //设置文件后缀
            responseFilename += TemplateUtils.getTemplateExcelType(importContext.getTemplate()).getValue();
            //设置响应头信息
            ResponseUtils.setDownloadFileHeader(response, responseFilename);
            InputStream templateInputStream = TemplateUtils.getTemplateInputStream(importContext.getImportProperties().getTemplateDir(),
                    importContext.getTemplate());
            FileCopyUtils.copy(templateInputStream, response.getOutputStream());
        } else {
            //设置文件后缀
            responseFilename += ExcelType.XLSX.getValue();
            //设置响应头信息
            ResponseUtils.setDownloadFileHeader(response, responseFilename);
            importAdapter.responseTemplate(importContext, response.getOutputStream());
        }
    }

    /**
     * 读取数据
     *
     * @param importContext
     * @param importAdapter
     * @return
     */
    @Override
    public Collection<?> readData(ImportContext importContext, ImportAdapter importAdapter) throws IOException, ExecutionException, InterruptedException, TimeoutException, ServletException {
        Part file = WebUtils.getCurrentRequest().getPart(importContext.getFileParam());

        if (file == null) {
            throw new IllegalArgumentException("没有读取到文件参数");
        }

        //获取导入数据
        return importAdapter.read(importContext, file.getInputStream());
    }

    /**
     * 数据转换
     *
     * @param importContext
     * @param data
     * @return
     */
    @Override
    public Object convertData(ImportContext importContext, Collection<?> data) {
        if (data == null) {
            return null;
        }

        //
        Class<?> containerClass = importContext.getTargetArgumentContainerClass();

        //如果当前数据类型已经是要求的类型的子类型，则直接返回当前数据
        if (containerClass.isAssignableFrom(data.getClass())) {
            return data;
        }

        //如果当前数据类型是数组类型
        if (containerClass.isArray()) {
            //将集合转换为数组返回
            return data.stream().toArray(value -> (Object[]) Array.newInstance(containerClass.getComponentType(), value));
        }

        //如果当前数据类型是集合类型
        if (Collection.class.isAssignableFrom(containerClass)) {
            //当前容器类型是接口类型
            if (containerClass.isInterface()) {
                //Set类型
                if (Set.class.isAssignableFrom(containerClass)) {
                    return new HashSet<>(data);
                }
                //List类型
                if (List.class.isAssignableFrom(containerClass)) {
                    return new ArrayList<>(data);
                }
                //其他类型待补充
            }
            //如果不是抽象类型
            if ((containerClass.getModifiers() & Modifier.ABSTRACT) == 0) {
                //new 一个实例出来
                try {
                    Collection<Object> collection = (Collection<Object>) containerClass.newInstance();
                    return data.stream().collect(Collectors.toCollection(() -> collection));
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("导入生成接口参数实例错误", e);
                }
            }
        }

        //数据转换失败
        throw new IllegalArgumentException();
    }

    /**
     * 生成请求参数
     *
     * @param importContext
     * @param joinPoint
     * @param data
     * @return
     */
    @Override
    public Object[] buildArguments(ImportContext importContext, ProceedingJoinPoint joinPoint, Object data) {
        Object[] args = joinPoint.getArgs();
        //替换对应位置参数
        args[importContext.getTargetArgumentIndex()] = data;
        return args;
    }
}
