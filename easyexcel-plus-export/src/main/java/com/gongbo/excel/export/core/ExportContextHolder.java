package com.gongbo.excel.export.core;


import com.gongbo.excel.export.entity.ExportContext;
import com.gongbo.excel.export.param.ExportParam;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExportContextHolder {

    private static final ThreadLocal<ExportContext> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 当前接口是否正在执行导出
     *
     * @return
     */
    public static boolean isExportExcel() {
        return Optional.ofNullable(getContext())
                .map(ExportContext::getExportParam)
                .map(ExportParam::isExcel)
                .orElse(false);
    }

    /**
     * 获取导出上下文
     *
     * @return
     */
    public static ExportContext getContext() {
        return THREAD_LOCAL.get();
    }

    /**
     * 设置导出上下文
     *
     * @param exportContext
     */
    public static void setContext(ExportContext exportContext) {
        THREAD_LOCAL.set(exportContext);
    }

    /**
     * 清空
     */
    public static void clear() {
        THREAD_LOCAL.remove();
    }

}
