package com.gongbo.excel.export.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExportUtils {

    /**
     * 数组或集合类型转化为List类型
     *
     * @param result
     * @return
     */
    public static List<?> objectToList(Object result) {
        if (result == null) {
            return Collections.emptyList();
        }

        if (result instanceof Collection) {
            if (result instanceof List) {
                return (List<?>) result;
            } else {
                return new ArrayList<>((Collection<?>) result);
            }
        } else if (result.getClass().isArray()) {
            return Arrays.asList((Object[]) result);
        } else if (result instanceof Iterable) {
            //支持返回Iterable
            Iterable<?> iterable = (Iterable<?>) result;
            List list = new ArrayList<>();
            iterable.forEach(list::add);
            return list;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 获取集合或数组的类型
     *
     * @param type
     * @return
     */
    public static Class<?> getComponentType(Type type) {
        //如果泛型参数是泛型类型
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() instanceof Class) {
                Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                if (Iterable.class.isAssignableFrom(rawType)) {
                    Type actualType2 = parameterizedType.getActualTypeArguments()[0];
                    if (actualType2 instanceof Class) {
                        return (Class<?>) actualType2;
                    } else if (actualType2 instanceof WildcardType) {
                        return null;
                    }
                }
            }
        }
        //如果泛型参数是泛数组类型
        else if (type instanceof Class && ((Class<?>) type).isArray()) {
            return ((Class<?>) type).getComponentType();
        }
        return null;
    }
}
