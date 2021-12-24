package com.gongbo.excel.common.utils;

import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * bean转换工具类
 */
public interface BeanMap {

    /**
     * 转换为其他类型对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    default <T> T mapTo(Class<T> clazz) {
        return mapTo(() -> BeanUtils.instantiateClass(clazz));
    }

    /**
     * 转换为其他类型对象
     *
     * @param supplier
     * @param <T>
     * @return
     */
    default <T> T mapTo(Supplier<T> supplier) {
        return mapTo(this, supplier);
    }

    /**
     * 转换为其他类型对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    static <T> T mapTo(Object obj, Class<T> clazz) {
        return mapTo(obj, () -> BeanUtils.instantiateClass(clazz));
    }

    /**
     * 转换为其他类型对象
     *
     * @param supplier
     * @param <T>
     * @return
     */
    static <T> T mapTo(Object obj, Supplier<T> supplier) {
        if (obj == null) return null;
        T data = supplier.get();
        BeanUtils.copyProperties(obj, data);
        return data;
    }

    static <T> T mapTo(Object obj, Supplier<T> supplier, Class<T> clazz) {
        if (obj == null) return null;
        if (supplier != null) {
            return mapTo(obj, supplier);
        }
        return mapTo(obj, clazz);
    }

    /**
     * 转换为其他类型对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    static <T> List<T> mapListTo(List<?> list, Class<T> clazz) {
        if (list == null) return null;
        if (list.isEmpty()) return Collections.emptyList();
        return list.stream().map(o -> mapTo(o, clazz)).collect(Collectors.toList());
    }

    /**
     * 转换为其他类型对象
     *
     * @param supplier
     * @param <T>
     * @return
     */
    static <T> List<T> mapListTo(List<?> list, Supplier<T> supplier) {
        if (list == null) return null;
        if (list.isEmpty()) return Collections.emptyList();
        return list.stream().map(o -> mapTo(o, supplier)).collect(Collectors.toList());
    }
}
