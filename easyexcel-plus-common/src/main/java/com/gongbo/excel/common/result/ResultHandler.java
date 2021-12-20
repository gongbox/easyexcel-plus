package com.gongbo.excel.common.result;

public interface ResultHandler<T> {

    /**
     * 返回结果包装类
     *
     * @return
     */
    Class<T> resultClass();

    /**
     * @param result
     * @return
     */
    default boolean check(Object result) {
        return resultClass().isInstance(result);
    }

    /**
     * @param result
     * @return
     */
    Object getData(T result);
}
