package com.gongbo.excel.export.core;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExportHandlers {
    /**
     * Cache
     */
    private static final Map<Class<?>, Object> handlerCache = new ConcurrentHashMap<>();

    /**
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T of(Class<T> clazz) {
        return (T) handlerCache.computeIfAbsent(clazz, BeanUtils::instantiateClass);
    }
}
