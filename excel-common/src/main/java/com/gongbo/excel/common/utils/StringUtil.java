package com.gongbo.excel.common.utils;

public class StringUtil {
    /**
     * @param text
     * @return
     */
    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    /**
     * @param text
     * @return
     */
    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    /**
     * 获取第一个非空的字符串，如果所有对象都为空，则返回null
     */
    public static String firstNotEmpty(String... values) {
        for (String value : values) {
            if (isNotEmpty(value)) {
                return value;
            }
        }
        return null;
    }

}
