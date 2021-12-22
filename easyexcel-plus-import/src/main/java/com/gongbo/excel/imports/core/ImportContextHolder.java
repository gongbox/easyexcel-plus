package com.gongbo.excel.imports.core;


import com.gongbo.excel.imports.entity.ImportContext;
import com.gongbo.excel.imports.param.ImportParam;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImportContextHolder {

    private static final ThreadLocal<ImportContext> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 当前接口是否正在执行导出
     *
     * @return
     */
    public static boolean isImport() {
        return Optional.ofNullable(getContext())
                .map(ImportContext::getImportParam)
                .map(ImportParam::isExcel)
                .orElse(false);
    }

    /**
     * 获取导出上下文
     *
     * @return
     */
    public static ImportContext getContext() {
        return THREAD_LOCAL.get();
    }

    /**
     * 设置导出上下文
     *
     * @param exportContext
     */
    public static void setContext(ImportContext exportContext) {
        THREAD_LOCAL.set(exportContext);
    }

    /**
     * 清空
     */
    public static void clear() {
        THREAD_LOCAL.remove();
    }

}
