package com.gongbo.excel.imports.core;

import com.gongbo.excel.imports.annotations.EnableImport;
import com.gongbo.excel.imports.entity.ImportContext;
import com.gongbo.excel.imports.entity.ImportParam;
import com.gongbo.excel.imports.exception.NotSupportImportException;
import com.gongbo.excel.imports.utils.ImportUtils;
import com.gongbo.excel.common.result.ResultHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.text.MessageFormat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImportHelper {

    /**
     * 构建导入上下文
     */
    public static ImportContext buildImportContext(ImportParam importParam, Method targetMethod, ResultHandler resultHandler) {
        EnableImport autoEnableImport = targetMethod.getAnnotation(EnableImport.class);
        //检查是否支持导入
        if (autoEnableImport == null) {
            throw new NotSupportImportException(MessageFormat.format("该方法[{0}]不支持导入，若要开启导入，请在对应请求方法上配置EnableImport注解开启导入", targetMethod.getName()));
        }

        //目标参数位置
        Integer argIndex = ImportUtils.getImportTargetArgIndex(targetMethod);
        //目标参数类型
        Class<?> modelContainerClass = ImportUtils.getModelContainerClass(targetMethod, argIndex);
        //导入数据模型类
        Class<?> modelClass;
        if (autoEnableImport.modelClass() != Object.class) {
            modelClass = autoEnableImport.modelClass();
        } else {
            modelClass = ImportUtils.getModelClass(targetMethod, argIndex);

            if (modelClass == null) {
                throw new IllegalArgumentException("无法提取到导入模型参数，请检查导入方法或在EnableImport注解上添加modelClass属性！");
            }
        }

        String sheetName = autoEnableImport.sheetName();

        return ImportContext.builder()
                .templateFileName(autoEnableImport.templateFileName())
                .importParam(importParam)
                .autoEnableImport(autoEnableImport)
                .paramName(autoEnableImport.paramName())
                .sheetNo(autoEnableImport.sheetNo() >= 0 ? autoEnableImport.sheetNo() : null)
                .sheetName(sheetName)
                .argIndex(argIndex)
                .containerClass(modelContainerClass)
                .modelClass(modelClass)
                .build();
    }

}
