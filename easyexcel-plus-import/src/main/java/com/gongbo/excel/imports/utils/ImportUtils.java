package com.gongbo.excel.imports.utils;


import com.gongbo.excel.imports.annotations.ImportTarget;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
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
                throw new IllegalArgumentException("没有找到对应参数位置");
            }
            return null;
        }

        if (argIndex >= method.getParameterCount()) {
            throw new IllegalArgumentException("参数位置计算错误");
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

}
