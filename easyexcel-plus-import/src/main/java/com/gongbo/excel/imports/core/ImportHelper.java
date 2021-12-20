package com.gongbo.excel.imports.core;

import com.gongbo.excel.common.result.ResultHandler;
import com.gongbo.excel.imports.annotations.ExcelImport;
import com.gongbo.excel.imports.entity.ImportContext;
import com.gongbo.excel.imports.entity.ImportParam;
import com.gongbo.excel.imports.exception.NotSupportImportException;
import com.gongbo.excel.imports.utils.ImportUtils;
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
        ExcelImport excelImport = targetMethod.getAnnotation(ExcelImport.class);
        //检查是否支持导入
        if (excelImport == null) {
            throw new NotSupportImportException(MessageFormat.format("this method:{0} not support import, to enable import, please configure ExcelImport annotation on the request method to enable import", targetMethod.getName()));
        }

        //目标参数位置
        boolean mustExists = !importParam.isTemplate() || excelImport.modelClass() == Object.class;
        Integer argIndex = ImportUtils.getImportTargetArgIndex(targetMethod, mustExists);
        //目标参数类型
        Class<?> modelContainerClass = argIndex == null ? null : ImportUtils.getModelContainerClass(targetMethod, argIndex);
        //导入数据模型类
        Class<?> modelClass;
        if (excelImport.modelClass() != Object.class) {
            modelClass = excelImport.modelClass();
        } else {
            modelClass = ImportUtils.getModelClass(targetMethod, argIndex);

            if (modelClass == null) {
                throw new IllegalArgumentException("unable to get the import model class, please check the import method or add the modelClass attribute to the ExcelImport annotation!");
            }
        }

        String sheetName = excelImport.sheetName();

        return ImportContext.builder()
                .templateFileName(excelImport.templateFileName())
                .importParam(importParam)
                .excelImport(excelImport)
                .paramName(excelImport.paramName())
                .sheetNo(excelImport.sheetNo() >= 0 ? excelImport.sheetNo() : null)
                .sheetName(sheetName)
                .argIndex(argIndex)
                .containerClass(modelContainerClass)
                .modelClass(modelClass)
                .build();
    }

}
