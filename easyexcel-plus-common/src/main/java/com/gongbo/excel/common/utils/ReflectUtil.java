package com.gongbo.excel.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectUtil {

    /**
     * @param beanClass
     * @param withSuperClassFields
     * @return
     * @throws SecurityException
     */
    public static List<Field> getFields(Class<?> beanClass, boolean withSuperClassFields) throws SecurityException {
        List<Field> fields = new ArrayList<>();

        for (Class<?> searchType = beanClass; searchType != null; searchType = withSuperClassFields ? searchType.getSuperclass() : null) {
            Field[] declaredFields = searchType.getDeclaredFields();
            fields.addAll(Arrays.asList(declaredFields));
        }
        return fields;
    }
}
