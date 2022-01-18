package com.gongbo.excel.export.core.resulthandler;

import java.lang.reflect.Method;

public interface ResultHandler {

    /**
     * 返回结果包装类
     *
     * @return
     */
    Class<?> resultClass();

    /**
     * 检查返回结果
     *
     * @return
     */
    boolean checkResultType(Class<?> type);

    /**
     * 获取模型类型
     *
     * @return
     */
    Class<?> getModelType(Method method);

    /**
     * 从返回中获取结果数据
     *
     * @param result
     * @return
     */
    Object getResultData(Object result);

}
