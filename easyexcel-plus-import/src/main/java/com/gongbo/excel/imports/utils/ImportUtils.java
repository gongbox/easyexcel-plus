package com.gongbo.excel.imports.utils;


import com.alibaba.excel.support.ExcelTypeEnum;
import com.gongbo.excel.common.utils.StringUtil;
import com.gongbo.excel.imports.annotations.ImportTarget;
import com.gongbo.excel.imports.entity.ImportContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImportUtils {

    /**
     * 获取参数位置
     */
    public static Integer getImportTargetArgIndex(Method method, boolean mustExists) {
        int parameterCount = method.getParameterCount();

        int argIndex = -1;

        if (parameterCount == 1) {
            argIndex = 0;
        } else if (parameterCount > 1) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                if (parameterAnnotations[i] != null && parameterAnnotations[i].length > 0) {
                    for (Annotation annotation : parameterAnnotations[i]) {
                        if (annotation.annotationType() == ImportTarget.class) {
                            argIndex = i;
                            break;
                        }
                    }
                }
            }
        }

        //检查
        if (argIndex < 0) {
            if (mustExists) {
                throw new IllegalArgumentException("not found import argument");
            }
            return null;
        }

        if (argIndex >= method.getParameterCount()) {
            throw new IllegalArgumentException("find import argument error");
        }

        return argIndex;
    }

    /**
     * 获取参数的参数类型
     */
    public static Class<?> getModelClass(Method method, Integer argIndex) {
        Type[] parameterTypes = method.getGenericParameterTypes();
        return getModelTypeClass(parameterTypes[argIndex]);
    }

    /**
     * 获取参数的容器类型
     */
    public static Class<?> getModelContainerClass(Method method, Integer argIndex) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return parameterTypes[argIndex];
    }


    /**
     * 构建容器
     */
    public static Collection<Object> buildCollectionContainer(Class containerClass) {
        //如果当前数据类型是集合类型
        if (Collection.class.isAssignableFrom(containerClass)) {
            //当前容器类型是接口类型
            if (containerClass.isInterface()) {
                //Set类型
                if (Set.class.isAssignableFrom(containerClass)) {
                    return new HashSet<>();
                }
                //List类型
                if (List.class.isAssignableFrom(containerClass)) {
                    return new ArrayList<>();
                }
                //其他类型待补充
            }
            //如果不是抽象类型
            if ((containerClass.getModifiers() & Modifier.ABSTRACT) == 0) {
                //new 一个实例出来
                try {
                    return (Collection<Object>) containerClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        //默认返回ArrayList
        return new ArrayList<>();
    }

    /**
     * 获取集合类型
     */
    private static Class<?> getModelTypeClass(Type parameterType) {
        if (parameterType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) parameterType;
            //获取泛型参数
            if (parameterizedType.getRawType() instanceof Class) {
                Class<?> rawClass = (Class<?>) parameterizedType.getRawType();
                if (Iterable.class.isAssignableFrom(rawClass)) {
                    Type actualType = parameterizedType.getActualTypeArguments()[0];
                    if (actualType instanceof Class) {
                        return (Class<?>) actualType;
                    } else if (actualType instanceof WildcardType) {
                        //暂时不支持
                        return null;
                    }
                }
            }
        }  //如果参数是数组类型
        else if (parameterType instanceof Class && ((Class<?>) parameterType).isArray()) {
            return ((Class<?>) parameterType).getComponentType();
        }

        throw new IllegalArgumentException();
    }

    public static void addHeader(ImportContext importContext, HttpServletResponse response) {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.addHeader("Content-Type", "application/vnd.ms-excel;charset=UTF-8");

        String templateFileName = importContext.getTemplateFilename();

        if (StringUtil.isEmpty(templateFileName)) {
            templateFileName = String.valueOf(System.currentTimeMillis());
        }

        String excelFileSuffix = ExcelTypeEnum.XLSX.getValue();
        templateFileName = templateFileName + excelFileSuffix;

        try {
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            templateFileName = URLEncoder.encode(templateFileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException ignored) {
        }

        response.addHeader("Content-Disposition", "attachment;filename*=utf-8''" + templateFileName + excelFileSuffix);
    }
}
