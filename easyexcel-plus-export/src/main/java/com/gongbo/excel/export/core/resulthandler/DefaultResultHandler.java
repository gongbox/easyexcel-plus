package com.gongbo.excel.export.core.resulthandler;

import com.gongbo.excel.export.utils.ExportUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 默认结果转换器
 *
 * @return
 */
@ConditionalOnMissingBean(ResultHandler.class)
public class DefaultResultHandler implements ResultHandler {

    @Override
    public boolean checkResultType(Class<?> returnType) {
        if (returnType.isArray() || Iterable.class.isAssignableFrom(returnType)) {
            return true;
        }
        Class<?> resultClass = resultClass();
        if (resultClass != null) {
            return resultClass.isAssignableFrom(returnType);
        }
        return false;
    }

    @Override
    public Class<?> getModelType(Method method) {
        Class<?> returnType = method.getReturnType();

        //检查返回类型是否支持
        checkResultType(returnType);

        //如果不是包装类型（数组，集合，Iterable等）
        Type containerType;
        if (returnType.isArray()) {
            containerType = returnType;
        } else if (Iterable.class.isAssignableFrom(returnType)) {
            containerType = method.getGenericReturnType();
        } else {
            Type genericReturnType = method.getGenericReturnType();
            //不是泛型类型，则返回空
            if (!(genericReturnType instanceof ParameterizedType)) {
                return null;
            }
            ParameterizedType parameterizedReturnType = (ParameterizedType) genericReturnType;
            //获取泛型参数
            Type[] actualTypeArguments = parameterizedReturnType.getActualTypeArguments();
            containerType = actualTypeArguments[0];
        }
        return ExportUtils.getComponentType(containerType);
    }

    @Override
    public Class<?> resultClass() {
        return null;
    }

    @Override
    public Object getResultData(Object result) {
        //集合或迭代器等直接返回
        if (result instanceof Iterable) {
            return result;
        }
        //数组则直接返回
        if (result != null && result.getClass().isArray()) {
            return result;
        }

        return null;
    }
}
