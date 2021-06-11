package com.gongbo.excel.imports.core;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.gongbo.excel.imports.entity.ImportContext;
import com.gongbo.excel.imports.handler.DownloadTemplateHandler;
import com.gongbo.excel.imports.handler.ImportDataConvert;
import com.gongbo.excel.imports.handler.ReadHandler;
import com.gongbo.excel.imports.utils.ImportUtils;
import com.gongbo.excel.common.utils.StringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImportHandlers {
    /**
     * 缓存
     */
    private static final Map<Class<?>, Object> handlerCache = new ConcurrentHashMap<>();

    /**
     * 根据Class获取对应Handler
     */
    public static <T> T of(Class<T> clazz) {
        return (T) handlerCache.computeIfAbsent(clazz, BeanUtils::instantiateClass);
    }

    /**
     * 默认导入数据转换器
     */
    @Slf4j
    public static class DefaultImportDataConvert implements ImportDataConvert {

        @Override
        public Object convert(ImportContext importContext, Collection<?> data) {
            if (data == null) {
                return null;
            }

            //
            Class<?> containerClass = importContext.getContainerClass();

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
                        log.error("导入实例化数据类型失败", e);
                    }
                }
            }

            //数据转换失败
            throw new IllegalArgumentException();
        }
    }

    /**
     * 默认数据读取执行器
     */
    public static class DefaultReadHandler implements ReadHandler {

        @Override
        public CompletableFuture<Collection<?>> read(ImportContext importContext, InputStream inputStream) throws IOException {
            //导入数据临时存放容器
            Collection<Object> container = ImportUtils.buildCollectionContainer(importContext.getContainerClass());

            CompletableFuture<Collection<?>> completableFuture = new CompletableFuture<>();

            //导入读取监听器
            ImportReadListener readListener = new ImportReadListener(container,
                    (data, analysisContext) -> completableFuture.complete(data),
                    (exception, analysisContext) -> completableFuture.completeExceptionally(exception));

            //导入模型类
            Class<?> modelClass = importContext.getModelClass();

            ExcelReaderBuilder readerBuilder = EasyExcel.read(inputStream, modelClass, readListener);

            ExcelReaderSheetBuilder excelReaderSheetBuilder;

            //设置读取的sheet
            if (importContext.getSheetNo() != null && importContext.getSheetNo() >= 0) {
                excelReaderSheetBuilder = readerBuilder.sheet(importContext.getSheetNo());
            } else if (StringUtil.isNotEmpty(importContext.getSheetName())) {
                excelReaderSheetBuilder = readerBuilder.sheet(importContext.getSheetName());
            } else {
                excelReaderSheetBuilder = readerBuilder.sheet(0);
            }

            //读取
            excelReaderSheetBuilder.doRead();

            return completableFuture;
        }
    }

    public static class DefaultDownloadTemplateHandler implements DownloadTemplateHandler {

        @Override
        public void download(ImportContext importContext, HttpServletResponse response) throws IOException {
            //设置响应头信息
            addHeader(importContext, response);

            //生成导入模板
            ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(response.getOutputStream(), importContext.getModelClass());

            excelWriterBuilder.sheet(importContext.getSheetNo(), importContext.getSheetName())
                    .doWrite(null);
        }
    }
}
